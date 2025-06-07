package nieboczek.createpayforpower.block.directionalnetwork;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import nieboczek.createpayforpower.CreatePayForPower;
import nieboczek.createpayforpower.block.networkstresslimiter.NetworkStressLimiterBlock;
import nieboczek.createpayforpower.mixin.TorquePropagatorMixin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class DirectionalNetworkKineticBlockEntity extends KineticBlockEntity {
    // NOTE: A lot of code from the Create mod

    @Nullable
    Long inputNetwork;
    @Nullable Long outputNetwork;
    @Nullable BlockPos inputSource;
    @Nullable BlockPos outputSource;
    long inputStress;
    long outputStress;
    long inputCapacity;
    long outputCapacity;

    private int flickerTally;
    private int validationCountdown;

    // TODO: Make a value scroller for maxStress
    public float maxStress = 2000;  // TODO: change to something like 256 later

    // NOTE: Useful methods to use later
    // KineticNetwork#updateCapacityFor(KineticBlockEntity myNSLbe, float capacity)

    public DirectionalNetworkKineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        if (!level.isClientSide && needsSpeedUpdate())
            attachKinetics();

        super.tick();
        effects.tick();

        preventSpeedUpdate = 0;

        if (level.isClientSide) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> this::tickAudio);
            return;
        }

        if (validationCountdown-- <= 0) {
            validationCountdown = AllConfigs.server().kinetics.kineticValidationFrequency.get();
            validateKinetics();
        }

        if (getFlickerScore() > 0)
            flickerTally = getFlickerScore() - 1;

        if (networkDirty) {
            if (hasNetwork()) {
                getInputNetwork().updateNetwork();
                getOutputNetwork().updateNetwork();
            }
            networkDirty = false;
        }
    }

    private void validateKinetics() {
        // TODO: idk what should i do here really
        if (hasSource()) {
            if (!hasNetwork()) {
                removeSource();
                return;
            }

            if (!level.isLoaded(source))
                return;

            BlockEntity blockEntity = level.getBlockEntity(source);
            KineticBlockEntity sourceBE =
                    blockEntity instanceof KineticBlockEntity ? (KineticBlockEntity) blockEntity : null;
            if (sourceBE == null || sourceBE.getTheoreticalSpeed() == 0) {
                removeSource();
                detachKinetics();
                return;
            }

            return;
        }

        if (speed != 0) {
            if (getGeneratedSpeed() == 0)
                speed = 0;
        }
    }

    @Override
    public void setSource(BlockPos source) {
        BlockPos inputPos = getBlockPos().relative(getBlockState().getValue(NetworkStressLimiterBlock.FACING));
        boolean isInputSource = source.equals(inputPos);

        if (isInputSource)
            inputSource = source;
        else
            outputSource = source;

        this.source = source;
        if (level == null || level.isClientSide)
            return;

        BlockEntity entity = level.getBlockEntity(source);
        if (!(entity instanceof KineticBlockEntity sourceEntity)) {
            removeSource();
            return;
        }

        if (isInputSource)
            setInputNetwork(sourceEntity.network);
        else
            setOutputNetwork(sourceEntity.network);

        copySequenceContextFrom(sourceEntity);
    }

    @Override
    public void removeSource() {
        // TODO: with no idea in what situation are we, we can't really adjust behavior
        // cases to handle:
        // we now output more capacity than other sources (outputSource)
        // lost source (inputSource)
        // validation (see validateKinetics), for now I don't know what to do with this
        // setSource (can get easily)
        float prevSpeed = getSpeed();

        speed = 0;
        source = null;
        setInputNetwork(null);
        sequenceContext = null;

        onSpeedChanged(prevSpeed);
    }

//    public void updateOutput() {
//        if (hasOutputNetwork()) {
//            BlockEntity entity = level.getBlockEntity(getBlockPos().relative(Direction.EAST));
//            if (entity instanceof KineticBlockEntity kineticEntity) {
//                // TODO: Do a better check for this here
//                if (!kineticEntity.getBlockState().hasProperty(ShaftBlock.AXIS)) return;
//
//                // TODO: Update this entire section
//                Direction.Axis myAxis = getBlockState().getValue(BlockStateProperties.FACING).getAxis();
//                Direction.Axis outputAxis = kineticEntity.getBlockState().getValue(BlockStateProperties.AXIS);
//
//                if (myAxis != outputAxis) {
//                    return;
//                }
//
//                if (kineticEntity.network == null) {
//                    kineticEntity.setNetwork(kineticEntity.getBlockPos().asLong());
//                    CreatePayForPower.LOGGER.info("Created kinetic network for output.");
//                }
//
//                kineticEntity.setSpeed(getSpeed());
////                kineticEntity.setSource(getBlockPos());  // this is the most recent addition, the one (probably) breaking the world
//
//                KineticNetwork outputNetwork = kineticEntity.getOrCreateNetwork();
//                outputNetwork.add(this);
////                outputNetwork.updateNetwork();  // this is the second most recent addition
//
//                CreatePayForPower.LOGGER.info("Added self to output's kinetic network");
//                CreatePayForPower.LOGGER.info("cool stats: {} RPM, stress: {}su, capacity: {}su", kineticEntity.getSpeed(), outputNetwork.calculateStress(), outputNetwork.calculateCapacity());
//            }
//        }
//    }

    @Override
    public void initialize() {
//        updateOutput();
        super.initialize();
    }

    public KineticNetwork getInputNetwork() {
        KineticNetwork network;
        Map<Long, KineticNetwork> map = TorquePropagatorMixin.getNetworks().computeIfAbsent(getLevel(), $ -> new HashMap<>());

        if (inputNetwork == null)
            return null;

        if (!map.containsKey(inputNetwork)) {
            network = new KineticNetwork();
            network.id = inputNetwork;
            map.put(inputNetwork, network);
        }

        network = map.get(inputNetwork);
        return network;
    }

    public KineticNetwork getOutputNetwork() {
        KineticNetwork network;
        Map<Long, KineticNetwork> map = TorquePropagatorMixin.getNetworks().computeIfAbsent(getLevel(), $ -> new HashMap<>());

        if (outputNetwork == null)
            return null;

        if (!map.containsKey(outputNetwork)) {
            network = new KineticNetwork();
            network.id = outputNetwork;
            map.put(outputNetwork, network);
        }

        network = map.get(outputNetwork);
        return network;
    }

    public void setInputNetwork(@Nullable Long networkIn) {
        if (Objects.equals(inputNetwork, networkIn))
            return;
        if (inputNetwork != null)
            getOrCreateNetwork().remove(this);

        inputNetwork = networkIn;
        setChanged();

        if (networkIn == null)
            return;

        inputNetwork = networkIn;
        KineticNetwork network = getInputNetwork();
        network.initialized = true;
        network.add(this);
    }

    public void setOutputNetwork(@Nullable Long networkIn) {
        if (Objects.equals(outputNetwork, networkIn))
            return;
        if (outputNetwork != null)
            getOrCreateNetwork().remove(this);

        outputNetwork = networkIn;
        setChanged();

        if (networkIn == null)
            return;

        outputNetwork = networkIn;
        KineticNetwork network = getOutputNetwork();
        network.initialized = true;
        network.add(this);
    }

    public boolean hasOutputNetwork() {
        return getOutputNetwork() != null;
    }

    @Override
    public KineticNetwork getOrCreateNetwork() {
        CreatePayForPower.LOGGER.warn("getOrCreateNetwork used on NSL D:");
        return null;
    }

    @Override
    public void setNetwork(@Nullable Long networkIn) {
        CreatePayForPower.LOGGER.warn("setNetwork used on NSL D:");
    }

    @Override
    public boolean hasNetwork() {
        CreatePayForPower.LOGGER.warn("hasNetwork used on NSL. Returning OR of input & output");
        return getInputNetwork() != null || getOutputNetwork() != null;
    }

    @Override
    public boolean hasSource() {
        CreatePayForPower.LOGGER.warn("hasSource used on NSL. Returning OR of input & output");
        return inputSource != null || outputSource != null;
    }
}

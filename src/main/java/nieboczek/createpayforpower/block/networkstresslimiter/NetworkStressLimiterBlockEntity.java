package nieboczek.createpayforpower.block.networkstresslimiter;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import nieboczek.createpayforpower.CreatePayForPower;

public class NetworkStressLimiterBlockEntity extends KineticBlockEntity {
    public float maxStress = 2000;  // TODO: change to something like 256 later

    // NOTE: Useful methods to use later
    // KineticNetwork#updateCapacityFor(KineticBlockEntity myNSLbe, float capacity)

    public NetworkStressLimiterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
//        GeneratingKineticBlockEntity
    }

    // TODO: might want to override these methods
    //	@Override
    //	public void removeSource() {
    //		if (hasSource() && isSource())
    //			reActivateSource = true;
    //		super.removeSource();
    //	}
    //
    //	@Override
    //	public void setSource(BlockPos source) {
    //		super.setSource(source);
    //		BlockEntity blockEntity = level.getBlockEntity(source);
    //		if (!(blockEntity instanceof KineticBlockEntity sourceBE))
    //			return;
    //		if (reActivateSource && Math.abs(sourceBE.getSpeed()) >= Math.abs(getGeneratedSpeed()))
    //			reActivateSource = false;
    //	}

    protected void updateOutput() {
        if (hasNetwork()) {
            BlockEntity entity = getLevel().getBlockEntity(getBlockPos().relative(Direction.EAST));
            if (entity instanceof KineticBlockEntity kineticEntity) {
                // FIXME: remove this if statement and debug the hell is happening here lol
                if (true) {
                    return;
                }

                if (!kineticEntity.getBlockState().hasProperty(ShaftBlock.AXIS)) {
                    return;
                }

                Direction.Axis myAxis = getBlockState().getValue(NetworkStressLimiterBlock.FACING).getAxis();
                Direction.Axis outputAxis = kineticEntity.getBlockState().getValue(ShaftBlock.AXIS);

                if (myAxis != outputAxis) {
                    return;
                }

                if (kineticEntity.network == null) {
                    kineticEntity.setNetwork(kineticEntity.getBlockPos().asLong());
                    CreatePayForPower.LOGGER.info("Created kinetic network for output.");
                }

                kineticEntity.setSpeed(getSpeed());
                kineticEntity.setSource(getBlockPos());  // this is the most recent addition, the one (probably) breaking the world

                KineticNetwork outputNetwork = kineticEntity.getOrCreateNetwork();
                outputNetwork.add(this);
                outputNetwork.updateNetwork();  // this is the second most recent addition

                CreatePayForPower.LOGGER.info("Added self to output's kinetic network");
                CreatePayForPower.LOGGER.info("cool stats: {} RPM, stress: {}su, capacity: {}su", kineticEntity.getSpeed(), outputNetwork.calculateStress(), outputNetwork.calculateCapacity());
            }
        }
    }

    @Override
    public void initialize() {
        updateOutput();
    }

    @Override
    public float getGeneratedSpeed() {
        return 1;
    }
}

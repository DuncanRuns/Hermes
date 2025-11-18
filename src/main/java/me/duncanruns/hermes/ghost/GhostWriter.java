package me.duncanruns.hermes.ghost;

import me.duncanruns.hermes.HermesMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * It's possible the format could change, and anything that reads the file should know the version of Hermes that wrote it.
 */
public class GhostWriter {
    private static final int PACKET_SIZE = 42;
    private final Path path;
    private final Path requiredParent;
    private boolean isCreated = false;
    private RandomAccessFile file;
    private boolean full = false;

    private final ByteBuffer buffer;

    public GhostWriter(MinecraftServer server, UUID playerId) {
        Path worldPath = HermesMod.getSavePath(server);
        this.path = worldPath.resolve("hermes").resolve("ghosts").resolve(playerId.toString() + ".ghost");
        this.requiredParent = worldPath;
        buffer = ByteBuffer.wrap(new byte[PACKET_SIZE * 20 * 60 * 10]); // 0.48 MB, 10 minutes of data
        buffer.position(0);
    }

    private static byte getFlags(ServerPlayerEntity player) {
        //? if >=1.16 {
        boolean isHandSwinging = player.handSwinging;
        //?} else {
        /*boolean isHandSwinging = player.isHandSwinging;
         *///?}
        byte swinging = (byte) (isHandSwinging ? (0x01) : 0);
        byte usingItem = (byte) (player.isUsingItem() ? (0x02) : 0);
        byte sneaking = (byte) (player.isSneaking() ? (0x04) : 0);
        byte sprinting = (byte) (player.isSprinting() ? (0x08) : 0);
        byte isAttacked = (byte) (player.hurtTime > 0 ? (0x10) : 0);
        byte isAlive = (byte) (player.isAlive() ? (0x20) : 0);
        byte fallFlying = (byte) (player.isFallFlying() ? (0x40) : 0); // 1.9+
        byte swimming = (byte) (player.isSwimming() ? (0x80) : 0); // 1.13+
        return (byte) (swinging | usingItem | sneaking | sprinting | isAttacked | isAlive | fallFlying | swimming);
    }

    public void onTick(long time, ServerPlayerEntity player) {
        if (full) return;
        if (buffer.position() + PACKET_SIZE > buffer.array().length) {
            HermesMod.LOGGER.warn("Ghost buffer is full for {}! Won't be able to track more positions until next save.", path);
            full = true;
            return;
        }
        buffer.putLong(time); // 1->8
        Vec3d pos = player.getPos();
        buffer.putDouble(pos.x); // 9->16
        buffer.putDouble(pos.y); // 17->24
        buffer.putDouble(pos.z); // 25->32
        buffer.putFloat(player.headYaw); // 33->36
        buffer.putFloat(player.pitch); // 37->40
        buffer.put((byte) (player.inventory.selectedSlot % 9)); // 41
        buffer.put(getFlags(player)); // 42
    }

    public void onSave() {
        if (buffer.position() == 0) return;
        try {
            if (!isCreated) {
                if (!Files.isDirectory(requiredParent)) return;
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                file = new RandomAccessFile(path.toFile(), "rw");
                file.seek(file.length());
                isCreated = true;
            }
            file.write(buffer.array(), 0, buffer.position());
            buffer.position(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        onSave();
        try {
            if (file != null) {
                file.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

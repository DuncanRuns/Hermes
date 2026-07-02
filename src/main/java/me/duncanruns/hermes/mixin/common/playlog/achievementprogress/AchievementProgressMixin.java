//? if >=1.7 <=1.11.2 {
/*package me.duncanruns.hermes.mixin.common.playlog.achievementprogress;

import com.google.common.collect.ForwardingSet;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.playlog.achievementprogress.ExtendedStatInfo;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.AchievementProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({"unchecked", "rawtypes"})
@Mixin(AchievementProgress.class)
public abstract class AchievementProgressMixin extends ForwardingSet implements ExtendedStatInfo {
    @Unique
    private PlayerEntity player;
    @Unique
    private String achievementName;
    @Unique
    private boolean reloading = false;

    @Override
    public void hermes$setInfo(PlayerEntity player, String achievementName) {
        this.player = player;
        this.achievementName = achievementName;
    }

    //? if >1.7.2 {
    @WrapMethod(method = "update")
    private void updateWithSuper(JsonElement progress, Operation<Void> original) {
        reloading = true;
        original.call(progress);
        reloading = false;
    }
    //?}

    @Override
    public boolean add(Object element) {
        if (reloading) return super.add(element);
        if (!contains(element) && player instanceof ServerPlayerEntity) {
            MinecraftServer server = ((ServerPlayerEntity) player).getServerWorld().getServer();
            String progressName = element.toString();
            PlayLogHelper.getPlayLog(server).ifPresent(playLog -> playLog.onAchievementProgress(player, achievementName, progressName));
        }
        return super.add(element);
    }
}
*///?}
package com.blamejared.itemmagnet;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MagnetSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    private final Query<EntityStore> query;

    public MagnetSystem() {
        this.query = Query.and(Player.getComponentType());
    }

    private boolean isMagnet(@Nullable ItemStack stack) {
        return stack != null && stack.getItemId().equals(ItemMagnet.MAGNET_ITEM_ID);
    }

    private boolean hasMagnet(Inventory inventory) {
        if (inventory != null) {

            if (isMagnet(inventory.getActiveHotbarItem()) || isMagnet(inventory.getUtilityItem())) {
                return true;
            }

            if (ItemMagnet.INSTANCE.config.get().anywhereInUtility()) {
                ItemContainer slots = inventory.getUtility();
                for (short s = 0; s < slots.getCapacity(); s++) {
                    ItemStack itemStack = slots.getItemStack(s);
                    if (isMagnet(itemStack)) {
                        return true;
                    }
                }
            }

            if (ItemMagnet.INSTANCE.config.get().anywhereInInventory()) {
                ItemContainer slots = inventory.getCombinedHotbarFirst();
                for (short s = 0; s < slots.getCapacity(); s++) {
                    ItemStack itemStack = slots.getItemStack(s);
                    if (isMagnet(itemStack)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void tick(
            float dt,
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer
    ) {
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);

        Player player = EntityUtils.toHolder(index, archetypeChunk).getComponent(Player.getComponentType());
        if (player != null && hasMagnet(player.getInventory())) {
            TransformComponent playerTransform = commandBuffer.getComponent(playerRef, TransformComponent.getComponentType());
            ModelComponent modelcomponent = commandBuffer.getComponent(playerRef, ModelComponent.getComponentType());
            if (playerTransform == null || modelcomponent == null) {
                return;
            }

            Vector3d playerPos = playerTransform.getPosition().clone().add(0, modelcomponent.getModel().getEyeHeight(), 0);
            List<Ref<EntityStore>> nearby = new ArrayList<>();
            SpatialResource<Ref<EntityStore>, EntityStore> itemSpatialResource = commandBuffer.getResource(EntityModule.get().getItemSpatialResourceType());
            itemSpatialResource.getSpatialStructure().collect(playerPos, ItemMagnet.INSTANCE.config.get().pickupRadius(), nearby);
            for (Ref<EntityStore> entityStoreRef : nearby) {
                TransformComponent entityPos = commandBuffer.getComponent(entityStoreRef, TransformComponent.getComponentType());
                ItemComponent pickup = commandBuffer.getComponent(entityStoreRef, ItemComponent.getComponentType());
                if (entityPos != null && pickup != null && pickup.canPickUp()) {
                    entityPos.getPosition().assign(Vector3d.lerp(entityPos.getPosition(), playerPos, ItemMagnet.INSTANCE.config.get().moveSpeed()));
                }
            }
        }
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}

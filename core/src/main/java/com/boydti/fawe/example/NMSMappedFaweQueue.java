package com.boydti.fawe.example;

import com.boydti.fawe.config.Settings;
import com.boydti.fawe.object.FaweChunk;
import com.boydti.fawe.util.FaweQueue;
import com.boydti.fawe.util.TaskManager;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class NMSMappedFaweQueue<WORLD, CHUNK, CHUNKSECTION, SECTION> extends MappedFaweQueue<WORLD, CHUNKSECTION, SECTION> {
    public NMSMappedFaweQueue(String world) {
        super(world);
    }

    @Override
    public void sendChunk(final FaweChunk fc) {
        TaskManager.IMP.taskSyncSoon(new Runnable() {
            @Override
            public void run() {
                final boolean result = fixLighting(fc, Settings.FIX_ALL_LIGHTING ? FaweQueue.RelightMode.OPTIMAL : FaweQueue.RelightMode.MINIMAL) || !Settings.ASYNC_LIGHTING;
                TaskManager.IMP.taskSyncNow(new Runnable() {
                    @Override
                    public void run() {
                        if (!result) {
                            fixLighting(fc, Settings.FIX_ALL_LIGHTING ? FaweQueue.RelightMode.OPTIMAL : FaweQueue.RelightMode.MINIMAL);
                        }
                        CHUNK chunk = (CHUNK) fc.getChunk();
                        refreshChunk(getWorld(), chunk);
                    }
                }, false);
            }
        }, Settings.ASYNC_LIGHTING);
    }

    public abstract void refreshChunk(WORLD world, CHUNK chunk);

    public abstract CharFaweChunk getPrevious(CharFaweChunk fs, CHUNKSECTION sections, Map<?, ?> tiles, Collection<?>[] entities, Set<UUID> createdEntities, boolean all) throws Exception;
}

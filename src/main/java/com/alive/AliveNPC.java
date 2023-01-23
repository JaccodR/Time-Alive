package com.alive;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;

public class AliveNPC {
    @Getter
    private final int npcIndex;

    @Getter
    private final String npcName;

    @Getter
    @Setter
    private NPC npc;

    @Getter
    @Setter
    private long timeAlive;


    AliveNPC(NPC npc)
    {
        this.npc = npc;
        this.npcName = npc.getName();
        this.npcIndex = npc.getIndex();
        this.timeAlive = 0;
    }
}

package nex.jsgaddon.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import nex.jsgaddon.scheduled.ScheduledTasksStatic;


public class Tick {

    @SubscribeEvent
    public void worldTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ScheduledTasksStatic.iterate();
        }
    }
}
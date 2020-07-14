package ch.fhnw.ip6.api;

import ch.fhnw.ip6.api.util.SimpleSystemInfo;
import ch.fhnw.ip6.common.dto.Planning;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static ch.fhnw.ip6.api.util.Utils.bytesToHumanReadableValue;
import static ch.fhnw.ip6.api.util.Utils.showCpuInfo;

@Component
@Setter
@Getter
@Slf4j
public class SolverContext {

    private LocalDateTime startTime;

    private int timeLimit; // in secondds

    private boolean isSolving;

    private Planning planning;

    private Timer timer;


    /**
     * Compares received planning with existing planning. Keeps the planning with the better score.
     *
     * @param planning
     */
    public void saveBestPlanning(Planning planning) {
        if (this.planning == null || this.planning.getCost() > planning.getCost()) {
            this.planning = planning;
        }
    }

    /**
     * Clears an existing planning and resets the isSolving to false
     */
    public void reset() {
        isSolving = false;
        planning = null;
    }

    public void setIsSolving(boolean isSolving) {
        if (isSolving) {
            if (this.timer != null) {
                this.timer.cancel();
            }
            this.timer = new Timer();
            SimpleSystemInfo info = SimpleSystemInfo.getInstance();
            log.info(String.format("SYSINFO: %s %s %s", info.getName(), info.getArch(), info.getVersion()));
            log.info("SYSINFO: Available processors: " + info.getAvailableProcessors());
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    SimpleSystemInfo info = SimpleSystemInfo.getInstance();
                    SystemInfo si = new SystemInfo();
                    GlobalMemory mem = si.getHardware().getMemory();
                    VirtualMemory virtualMemory = mem.getVirtualMemory();
                    log.info("SYSINFO: " + showCpuInfo(info));
                    log.info(String.format("SYSINFO: System memory: %s used, %s total, %s available ", bytesToHumanReadableValue(mem.getTotal() - mem.getAvailable()), bytesToHumanReadableValue(mem.getTotal()), bytesToHumanReadableValue(mem.getAvailable())));
                    log.info(String.format("SYSINFO: Virtual memory: %s used, %s total, %s available ", bytesToHumanReadableValue(virtualMemory.getSwapUsed()), bytesToHumanReadableValue(virtualMemory.getSwapTotal()), bytesToHumanReadableValue(virtualMemory.getSwapTotal() - virtualMemory.getSwapUsed())));

                }
            }, 0, 60000);

            this.isSolving = true;

        } else {
            this.timer.cancel();
            this.isSolving = false;
        }
    }

}

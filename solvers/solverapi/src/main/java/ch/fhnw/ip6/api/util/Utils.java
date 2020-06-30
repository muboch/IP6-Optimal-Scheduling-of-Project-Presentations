package ch.fhnw.ip6.api.util;


import org.springframework.core.env.Environment;

public class Utils {

    private static String[] BYTE_UNITS = {"B", "kB", "MB"};

    public static String bytesToHumanReadableValue(long valueInBytes) {
        int unitsIndex = 0;
        double value = valueInBytes;
        while (value > 1024d && unitsIndex < BYTE_UNITS.length - 1) {
            value /= 1024d;
            unitsIndex++;
        }
        return String.format("%d%s", Math.round(value), BYTE_UNITS[unitsIndex]);
    }

    public static String showCpuInfo(SimpleSystemInfo info) {
        double processCpu = 100 * info.getProcessCpuLoad();
        double systemCpu = 100 * info.getSystemCpuLoad();
        double avgLoad = info.getSystemLoadAverage();

        return (String.format("CPU: %4.1f%% (process), %4.1f%% (system total), avg. load: %.2f",
                processCpu, systemCpu, avgLoad));
    }
}

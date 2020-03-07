package net.cryptic_game.backend;

public interface DeviceWorkload extends TableModelId {

    Device getDevice();

    void setDevice(Device device);

    float getPerformanceCPU();

    void setPerformanceCPU(float performanceCPU);

    float getPerformanceGPU();

    void setPerformanceGPU(float performanceGPU);

    float getPerformanceRAM();

    void setPerformanceRAM(float performanceRAM);

    float getPerformanceDisk();

    void setPerformanceDisk(float performanceDisk);

    float getPerformanceNetwork();

    void setPerformanceNetwork(float performanceNetwork);

    float getUsageCPU();

    void setUsageCPU(float UsageCPU);

    float getUsageGPU();

    void setUsageGPU(float UsageGPU);

    float getUsageRAM();

    void setUsageRAM(float UsageRAM);

    float getUsageDisk();

    void setUsageDisk(float UsageDisk);

    float getUsageNetwork();

    void setUsageNetwork(float UsageNetwork);
}

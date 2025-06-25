import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 虚拟存储设备类
 * 模拟物理存储设备，管理存储块的分配和释放
 */
public class StorageDevice implements Serializable {
    // 存储设备标识符
    public String deviceId;                 
    // 存储块数组
    public StorageBlock[] storageBlocks;    
    // 存储块分配状态表 (0=空闲, 1=已分配)
    public int[] allocationMap;             
    // 存储设备空间状态
    private boolean storageExhausted;       

    /**
     * 创建新的存储设备实例
     * @param deviceId 存储设备标识符
     */
    public StorageDevice(String deviceId) {
        this.deviceId = deviceId;
        this.storageBlocks = new StorageBlock[StorageConstants.DISK_SIZE];
        this.allocationMap = new int[StorageConstants.DISK_SIZE];
        this.storageExhausted = false;
        
        // 初始化所有存储块
        for (int i = 0; i < StorageConstants.DISK_SIZE; i++) {
            this.storageBlocks[i] = new StorageBlock(i);
            this.allocationMap[i] = 0;
        }
    }

    /**
     * 获取已使用的存储块数量
     * @return 已使用的存储块数量
     */
    private int getUsedBlockCount() {
        int usedCount = 0;
        for (int allocation : allocationMap) {
            usedCount += allocation;
        }
        return usedCount;
    }

    /**
     * 分配指定数量的存储块
     * @param blockCount 需要分配的存储块数量
     * @return 分配的存储块索引列表
     */
    public ArrayList<Integer> diskAlloc(int blockCount) {
        ArrayList<Integer> allocatedBlocks = new ArrayList<>();
        int allocated = 0;
        
        for (int j = 0; j < StorageConstants.DISK_SIZE && allocated < blockCount; j++) {
            if (allocationMap[j] == 0) {
                allocatedBlocks.add(j);
                allocationMap[j] = 1;
                allocated++;
            }
        }
        return allocatedBlocks;
    }

    /**
     * 释放指定的存储块
     * @param blockIndices 要释放的存储块索引列表
     */
    public void diskFree(ArrayList<Integer> blockIndices) {
        for (Integer index : blockIndices) {
            storageBlocks[index].clearBlock();
            allocationMap[index] = 0;
        }
    }

    /**
     * 显示存储设备使用情况
     */
    public void diskUsage() {
        if (isFull()) {
            System.out.println("存储设备已完全占满。");
        } else {
            int usedCount = getUsedBlockCount();
            int usagePercentage = (int) (100.0 * usedCount / StorageConstants.DISK_SIZE);
            System.out.println("存储使用率: " + usagePercentage + "%");
            System.out.println("已分配块数: " + usedCount);
            System.out.println("可用块数: " + (StorageConstants.DISK_SIZE - usedCount));
        }
    }

    /**
     * 检查存储设备是否已满
     * @return 如果存储设备已满则返回true，否则返回false
     */
    public boolean isFull() {
        storageExhausted = (getUsedBlockCount() == StorageConstants.DISK_SIZE);
        return storageExhausted;
    }

    @Override
    public String toString() {
        return "StorageDevice{" +
                "deviceId='" + deviceId + '\'' +
                ", storageBlocks=" + Arrays.toString(storageBlocks) +
                ", allocationMap=" + Arrays.toString(allocationMap) +
                ", storageExhausted=" + storageExhausted +
                '}';
    }
}
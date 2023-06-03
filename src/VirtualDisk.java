import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class VirtualDisk implements Serializable {
    public String diskName;                 // 虚拟磁盘名
    public VirtualBlock[] diskBlocks;       // 虚拟块表
    public int[] blockUsage;                // 虚拟块占用表
    private boolean isFull;                 // 磁盘是否已满

    public VirtualDisk(String diskName) {
        this.diskName = diskName;
        this.diskBlocks = new VirtualBlock[DiskConst.DISK_SIZE];
        this.blockUsage = new int[DiskConst.DISK_SIZE];
        this.isFull = false;
        // 初始化块
        for (int i = 0; i < DiskConst.DISK_SIZE; i++) {
            this.diskBlocks[i] = new VirtualBlock(i);
            this.blockUsage[i] = 0;
        }
    }

    public ArrayList<Integer> diskAlloc(int num) {
        ArrayList<Integer> idxBuffer = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < DiskConst.DISK_SIZE; j++) {
                if (blockUsage[j] == 0) {
                    idxBuffer.add(j);
                    blockUsage[j] = 1;
                    break;
                }
            }
        }
        return idxBuffer;
    }

    public void diskFree(ArrayList<Integer> idxBuffer) {
        for (Integer idx : idxBuffer) {
            diskBlocks[idx].clearBlock();
            blockUsage[idx] = 0;
        }
    }

    public void diskUsage() {
        if (isFull()) {
            System.out.println("Disk is used up.");
        } else {
            int used = 0;
            for (int i = 0; i < DiskConst.DISK_SIZE; i++) {
                used += blockUsage[i];
            }
            int usageRate = (int) (100.0 * used / DiskConst.DISK_SIZE);
            System.out.println("Disk Usage: " + usageRate + "%");
            System.out.println("Used blocks: " + used);
            System.out.println("Free blocks: " + (DiskConst.DISK_SIZE - used));
        }
    }

    public boolean isFull() {
        isFull = true;
        for (int i = 0; i < DiskConst.DISK_SIZE; i++) {
            if (!diskBlocks[i].isUSED()) {
                isFull = false;
                break;
            }
        }
        return isFull;
    }

    @Override
    public String toString() {
        return "VirtualDisk{" +
                "diskName='" + diskName + '\'' +
                ", diskBlocks=" + Arrays.toString(diskBlocks) +
                ", blockUsage=" + Arrays.toString(blockUsage) +
                ", isFull=" + isFull +
                '}';
    }
}
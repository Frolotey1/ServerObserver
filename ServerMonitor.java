import java.io.*;
import java.util.Random;

public class ServerMonitor implements Subject, Observer {
    private final String name;
    private double cpuLoad;
    private double memoryUsage;
    Status status;
    public ServerMonitor(String name, double cpuLoad, double memoryUsage, Status status) {
        this.name = name;
        this.cpuLoad = cpuLoad;
        this.memoryUsage = memoryUsage;
        this.status = status;
        if(status == null) detach();
    }
    @Override
    public void attach() {
        Thread launch = new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                try {
                    info();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        launch.start();
    }
    private void setCpu() {
        this.cpuLoad = new Random((long) this.cpuLoad).nextDouble();
    }
    private Status getStatus() {
        return this.status;
    }
    private void setMemoryUsage() {
        this.memoryUsage = new Random((long) this.memoryUsage).nextDouble();
    }
    private double getCpu() {
        update();
        return this.cpuLoad;
    }
    private double getMemoryUsage() {
        update();
        return this.memoryUsage;
    }
    private String getName() {
        return this.name;
    }
    @Override
    public void detach() {
        System.err.println("Server: " + this.name + " is off");
    }
    @Override
    public void update() {
        setCpu();
        setMemoryUsage();
    }
    private void info() {
        if(status == Status.ONLINE && getCpu() > 85.0)
            System.out.printf("📧 Email to admin@company.com: Server {%s} CPU overload: {%f} procents",getName(),getCpu());
        else if(status == Status.OFFLINE)
            System.out.printf("📱 SMS to +7-999-123-45-67: Server {%s} is DOWN!\n",getName());
        else {
            try(BufferedWriter writeToJson = new BufferedWriter(new FileWriter("Data.json"))) {
                writeToJson.write(String.format("Server: {%s} Cpu: {%f} Memory: {%f} Status: {%s}",getName(),getCpu(),getMemoryUsage(),getStatus()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

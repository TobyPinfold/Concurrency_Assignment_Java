
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Client implements Runnable {

    private MyFileServer fileServer;
    private String clientName;

    public static void main(String[] args) {

        MyFileServer fileServer = new MyFileServer();

        for (int fileNumber = 0; fileNumber < 10; fileNumber++) {
            double randomNumber = Math.random();
            fileServer.create("file:" + fileNumber, "number : " + randomNumber);
        }

        new Thread(new Client(fileServer, "Bob")).start();
        new Thread(new Client(fileServer, "Dan")).start();
        new Thread(new Client(fileServer, "Paul")).start();
        new Thread(new Client(fileServer, "Aaron")).start();
        new Thread(new Client(fileServer, "John")).start();
        new Thread(new Client(fileServer, "Mikey")).start();
        new Thread(new Client(fileServer, "Chris")).start();
        new Thread(new Client(fileServer, "Liam")).start();
        new Thread(new Client(fileServer, "Bill")).start();
        new Thread(new Client(fileServer, "Fred")).start();

    }

    public Client(MyFileServer fileServer, String clientName) {
        this.fileServer = fileServer;
        this.clientName = clientName;
    }

    @Override
    public void run() {

        Set<String> allFiles = fileServer.availableFiles();
        int randomNumber = ThreadLocalRandom.current().nextInt(allFiles.size());


        boolean isWriting = ThreadLocalRandom.current().nextInt(2) == 1;

        Optional<File> selectedFile = Optional.empty();

        int index = 0;
        for (String filename : allFiles) {
            if (index == randomNumber) {
                System.out.println(clientName + " opening file: " + filename);
                if (isWriting) {
                    selectedFile = fileServer.open(filename, Mode.READWRITEABLE);
                } else {
                    selectedFile = fileServer.open(filename, Mode.READABLE);
                }
                break;
            }
            index++;
        }

        if (isWriting) {
            if (selectedFile.isPresent()) {
                File file = selectedFile.get();
                System.out.println(clientName + " writing to file: " + file.filename());
                file.write("Written by: " + this.clientName);

                System.out.println(clientName + " closing file: " + file.filename());
                fileServer.close(file);
            }
        } else {
            if (selectedFile.isPresent()) {
                File file = selectedFile.get();
                try {
                    System.out.println(clientName + " reading file: " + file.filename());
                    Thread.sleep(ThreadLocalRandom.current().nextInt(2) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(clientName + " closing file: " + file.filename());
                fileServer.close(file);
            }
        }

        try {
            System.out.println(clientName + " is waiting for a random amount of time");
            Thread.sleep(ThreadLocalRandom.current().nextInt(4) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

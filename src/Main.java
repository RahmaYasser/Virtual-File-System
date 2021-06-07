import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    static Directory root = new Directory();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);
        //first time running main

        System.out.println("enter number of blocks");
        int n;
        n = scanner.nextInt();
        Disk.availableSpace = n;
        root.setName("root");
        Disk.Blocks = new ArrayList<>((Arrays.asList(new Boolean[n]))); // for initializing false
        Collections.fill(Disk.Blocks, Boolean.FALSE); //same


        //loading from file
        /*try{
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("disk.ser"));
            root = (Directory) is.readObject();
            Disk.Blocks = (ArrayList<Boolean>) is.readObject();
            System.out.println(Disk.Blocks.get(0));
            is.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }*/

        while(true){
            System.out.println("enter command");
            String command = scanner2.nextLine();
            String[] commands = command.split(" ");
            if(commands[0].equals("CreateFile")){
                System.out.println("enter method num, 0 for contiguous, 1 for linked and 2 for indexed");
                int method = scanner.nextInt();
                int size = Integer.parseInt( commands[2]);
                if(commands[1]!=null )root.createFile(commands[1],size,method);
            }
            else if(commands[0].equals("CreateFolder")){
                if(commands[1]!=null)root.createDirectory(commands[1]);
            }
            else if(commands[0].equals("DeleteFile")) {
                if(commands[1]!=null)root.deleteFile(commands[1]);
            }
            else if(commands[0].equals("DisplayDiskStatus")){
                root.displayDiskStatus();
            }
            else if (commands[0].equals("DisplayDiskStructure")){
                root.displayDiskStructure();
            }
            else System.out.println("unexpected command!");
            System.out.println("press 1 to run more commands or 2 to save and exit");
            int choice = scanner.nextInt();
            if(choice == 2){
                //saving all
                try {
                    FileOutputStream fs = new FileOutputStream("disk.ser");
                    ObjectOutputStream os = new ObjectOutputStream(fs);
                    os.writeObject(root);
                    os.writeObject(Disk.Blocks);
                    os.close();
                } catch (Exception ex){
                    ex.printStackTrace();
                }

                break;
            }
            else if(choice != 1){
                System.out.println("bad choice!, exit without saving");
                break;
            }
        }



    }
}

import java.io.*;
import java.util.ArrayList;

public class Directory implements Serializable {
    public ArrayList<FileClass> files;
    public ArrayList<Directory> subDirectories;
    private String name;
    private void freeAllocation(FileClass file){
        for (int k = 0; k < file.getIndexes().size(); k++) { // for each block
            Disk.Blocks.set(file.getIndexes().get(k),false);
            Disk.availableSpace++;
        }
    }
    private void allocateSize(ArrayList<Integer> indexes){
        for (int i = 0; i < indexes.size(); i++) {
            Disk.Blocks.set(indexes.get(i),true);
        }
    }
    public int contiguousAllocation(int n){
        int maxFitSize =0, start=0,end,blockSize=0,resOfStart=0;
        boolean enoughSpace = false;
        while(start<Disk.Blocks.size()){
            if(!Disk.Blocks.get(start)){
                end = start;
                while (end<Disk.Blocks.size()&&!Disk.Blocks.get(end)){
                    end++;
                }
                if(end-start>=n){
                    blockSize = end-start;
                    resOfStart = start;
                    enoughSpace = true;
                }
                maxFitSize = Math.max(maxFitSize,blockSize);
                start = end;
            }
            start++;
        }
        if(!enoughSpace)return -1;
        return resOfStart;
    }
    public FileClass linkedAllocation(int n){
        ArrayList<Integer> indexes = new ArrayList<>();
        FileClass file = new FileClass();
        file.setIndexes(indexes);
        n++;
        for (int i = 0; i < Disk.Blocks.size(); i++) {
            if (n > 0 && !Disk.Blocks.get(i)) {
                //Disk.Blocks.set(i,true);
                indexes.add(i);
                n--;
            }
        }
        String storageDetails = indexes.get(0).toString();
        if(n==0){
            for (int i = 1; i < indexes.size(); i++) {
                storageDetails+= " " + indexes.get(i).toString() + "\n" + indexes.get(i).toString();
            }
            storageDetails += "   nil\n";
            file.setStorageDetails(storageDetails);
            return file;
        }
        return null;
    }
    public FileClass indexedAllocation(int n) {
        ArrayList<Integer> indexes = new ArrayList<>();
        FileClass file = new FileClass();
        file.setIndexes(indexes);
        n++;
        for (int i = 0; i < Disk.Blocks.size(); i++) {
            if (n > 0 && !Disk.Blocks.get(i)) {
                //Disk.Blocks.set(i,true);
                indexes.add(i);
                n--;
            }
        }
        String storageDetails = indexes.get(0).toString()+"   ";
        if(n==0){
            for (int i = 1; i < indexes.size(); i++) {
                storageDetails+= " " + indexes.get(i).toString() ;
            }

            file.setStorageDetails(storageDetails);
            return file;
        }
        return null;
    }
    public FileClass createFile(String path, int size, int method){
        String [] directories = path.split("/");
        int i=1;
        if(!directories[0].equals("root")) {
            System.out.println("invalid path");
            return null;
        }
        ArrayList<Directory> dirPointer = Main.root.subDirectories;
        Directory lastDir=Main.root;
        while(i<directories.length-1){
            boolean directoryExists = false;
            for (int j = 0; j < dirPointer.size() ; j++) {
                if(directories[i].equals(dirPointer.get(j).getName())){
                    directoryExists = true;
                    lastDir = dirPointer.get(j);
                    break;
                }
                dirPointer = dirPointer.get(j).subDirectories;

            }
            if(!directoryExists) {
                System.out.println("invalid path");
                return null;
            }
            i++;
        }
        for (int j = 0; j < lastDir.files.size(); j++) {
            if(directories[directories.length-1].equals(lastDir.files.get(j).getName())){
                System.out.println("file already exits in this path" );
                return null;
            }
        }
        if(method == 0){
            int res=contiguousAllocation(size);
            if( res!= -1) {
                Disk.availableSpace-=size;
                FileClass file = new FileClass();
                System.out.println("file saved.");
                file.setStorageDetails(String.format("%d %d",res,res+size-1));

                file.setName(directories[directories.length-1]);
                file.setAllocationType(0); // contiguous type
                lastDir.files.add(file);

                //allocate
                ArrayList<Integer> indexes = new ArrayList<>();
                for (int j = 0; j < size; j++) {
                    indexes.add(res+j);
                }
                file.setIndexes(indexes);
                allocateSize(indexes);
                return file;
            }
            else System.out.println("not enough space");
        }else if(method == 1){
            FileClass res= linkedAllocation(size);
            if( res== null) System.out.println("not enough space");
            else{
                Disk.availableSpace-=size;
                System.out.println("file saved.");
                res.setName(directories[directories.length-1]);
                res.setAllocationType(1); // contiguous type
                lastDir.files.add(res);
                //allocate
                allocateSize(res.getIndexes());
                return res;
            }
        }
        else if(method == 2){
            FileClass res= indexedAllocation(size);
            if( res== null) System.out.println("not enough space");
            else{
                Disk.availableSpace-=size;
                System.out.println("file saved.");
                res.setName(directories[directories.length-1]);
                res.setAllocationType(2); // contiguous type
                lastDir.files.add(res);
                //allocate
                allocateSize(res.getIndexes());
                return res;
            }
        }
        else {
            System.out.println("bad choice..");
            return null;
        }
        return null;
    }
    public Directory createDirectory(String path){
        String [] directories = path.split("/");
        int i=1;
        if(!directories[0].equals("root")) {
            System.out.println("invalid path");
            return null;
        }
        ArrayList<Directory> dirPointer = Main.root.subDirectories;
        Directory lastDir=Main.root;
        while(i< directories.length-1){
            boolean directoryExists = false;
            for (int j = 0; j < dirPointer.size() ; j++) {
                if(directories[i].equals(dirPointer.get(j).getName())){
                    directoryExists = true;
                    lastDir = dirPointer.get(j);
                    break;
                }
                dirPointer = dirPointer.get(j).subDirectories;

            }
            if(!directoryExists) {
                System.out.println("invalid path");
                return null;
            }
            i++;
        }
        for (int j = 0; j < lastDir.subDirectories.size(); j++) {
            if(directories[directories.length-1].equals(lastDir.subDirectories.get(j).getName())) {
                System.out.println("directory already exists in this path" );
                return null;
            }
        }
        Directory dir = new Directory();
        dir.setName(directories[directories.length-1]);
        lastDir.subDirectories.add(dir);
        return dir;
    }
    public boolean deleteFile(String path){
        String [] directories = path.split("/");
        int i=1;
        if(!directories[0].equals("root")) {
            System.out.println("invalid path");
            return false;
        }
        ArrayList<Directory> dirPointer = Main.root.subDirectories;
        Directory lastDir=Main.root;
        while(i<directories.length-1){
            boolean directoryExists = false;
            for (int j = 0; j < dirPointer.size() ; j++) {
                if(directories[i].equals(dirPointer.get(j).getName())){
                    directoryExists = true;
                    lastDir = dirPointer.get(j);
                    break;
                }
                dirPointer = dirPointer.get(j).subDirectories;
            }
            if(!directoryExists) {
                System.out.println("invalid path");
                return false;
            }
            i++;
        }
        for (int j = 0; j < lastDir.files.size(); j++) {
            if (directories[directories.length - 1].equals(lastDir.files.get(j).getName())) { //file exists, free allocation then delete obj
               freeAllocation(lastDir.files.get(j)); // free allocation in disk
                lastDir.files.remove(j);
                return true;
            }
        }
        return false;
    }
    public boolean deleteDirectory(String path){
        String [] directories = path.split("/");
        int i=1;
        if(!directories[0].equals("root")) {
            System.out.println("invalid path");
            return false;
        }
        ArrayList<Directory> dirPointer = Main.root.subDirectories;
        Directory lastDir=Main.root;
        while(i<directories.length-1){
            boolean directoryExists = false;
            for (int j = 0; j < dirPointer.size() ; j++) {
                if(directories[i].equals(dirPointer.get(j).getName())){
                    directoryExists = true;
                    lastDir = dirPointer.get(j);
                    break;
                }
                dirPointer = dirPointer.get(j).subDirectories;

            }
            if(!directoryExists) {
                System.out.println("invalid path");
                return false;
            }
            i++;
        }
        for (int j = 0; j < lastDir.subDirectories.size(); j++) {
            if(directories[directories.length-1].equals(lastDir.subDirectories.get(j).getName())) {
                for (int k = 0; k < lastDir.subDirectories.get(j).files.size(); k++) {
                    freeAllocation(lastDir.subDirectories.get(j).files.get(k));
                }
                lastDir.subDirectories.remove(j);
                return true;
            }
        }
        return false;
    }
    private void displayTree(Directory dir,int spacesLength){
        String space = " ";
        String spaces = space.repeat(spacesLength);
        for (int i = 0; i < dir.files.size(); i++) {
            System.out.println( spaces +"- "+dir.files.get(i).getName());
        }
        for (int i = 0; i < dir.subDirectories.size(); i++) {
            System.out.println( spaces +"+ "+dir.subDirectories.get(i).getName());
            displayTree(dir.subDirectories.get(i),spacesLength+3);
        }
    }
    public void displayDiskStructure(){
        System.out.println("+ root");
        displayTree(Main.root,3);
    }
    public void displayDiskStatus(){
        int emptySpace=0;
        for (int i = 0; i < Disk.Blocks.size(); i++) {
            if(!Disk.Blocks.get(i))emptySpace+=1;
        }
        System.out.print("total empty space = ");
        System.out.print(emptySpace);
        System.out.println("kb");

        System.out.print("total allocated space = ");
        System.out.print(Disk.Blocks.size()-emptySpace);
        System.out.println("kb");

        for (int i = 0; i < Disk.Blocks.size(); i++) {
            if(!Disk.Blocks.get(i)) System.out.println(String.format("%d block is empty",i));
            else System.out.println(String.format("%d block is allocated",i));
        }
    }
    public String grantUserPermission(String user_name,String path){
        if(Main.currentUser != 0) {
           return "you are not allowed to use this command";

        }
        //if user exists:
        boolean found = false;
        for (int i = 0; i <Main.userNamees.size(); i++) {
            if(Main.userNamees.get(i).equals(user_name)) {
                found= true;

                break; }
        }
        if(!found) return "user isn't found..";

        //if path exists:
        path+="/xxx";
        String [] directories = path.split("/");
        int i=1;
        if(!directories[0].equals("root")) {
            return ("invalid path");

        }
        ArrayList<Directory> dirPointer = Main.root.subDirectories;
        Directory lastDir=Main.root;
        while(i< directories.length-1){
            boolean directoryExists = false;
            for (int j = 0; j < dirPointer.size() ; j++) {
                if(directories[i].equals(dirPointer.get(j).getName())){
                    directoryExists = true;
                    lastDir = dirPointer.get(j);
                    break;
                }
                dirPointer = dirPointer.get(j).subDirectories;

            }
            if(!directoryExists) return ("invalid path");
            i++;
        }
        for (int j = 0; j < lastDir.subDirectories.size(); j++) {
            if(directories[directories.length-1].equals(lastDir.subDirectories.get(j).getName())) {
                System.out.println("valid path" );
                //TODO grant
                return "done..";
            }
        }
        return "something went wrong";
    }
    private void grant(String path){
        try {
            File file = new File("permissions.txt");
            FileReader


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory() {
        files = new ArrayList<FileClass>();
        subDirectories = new ArrayList<Directory>();
    }
    

}

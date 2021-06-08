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
                    enoughSpace = true;
                }
                if(blockSize>maxFitSize){
                    maxFitSize = blockSize;
                    resOfStart = start;
                }
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
        //n++;
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
    public FileClass createFile(String path, int size, int method,String userName){
        if(Main.currentUser!=0){
            if(!getPermission(path,userName,0)){
                System.out.println("you have no access to create file or folder");
                return null;
            }
        }
        String [] directories = path.split("/");
        if(!directories[0].equals("root")) {
            System.out.println("invalid path");
            return null;
        }
        ArrayList<Directory> subDirectories = Main.root.subDirectories;
        Directory parent = Main.root;
        for(int i=1;i<directories.length-1;){ //directory[1]
            boolean found2 = false;
            for (int j = 0; j < subDirectories.size(); j++) {
                if(directories[i].equals(subDirectories.get(j).getName())){
                    found2 = true;
                    parent = subDirectories.get(j);
                    subDirectories = subDirectories.get(j).subDirectories;
                    i++;
                    break;
                }
            }
            if(!found2){
                System.out.println("invalid path");
                return null;
            }
        }
        for (int j = 0; j < parent.files.size(); j++) {
            if (directories[directories.length-1].equals(parent.files.get(j).getName())) {
                System.out.println("file already exists");
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
                parent.files.add(file);

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
                parent.files.add(res);
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
                parent.files.add(res);
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
    public Directory createDirectory(String path,String userName){
        if(Main.currentUser!=0) {
            if (!getPermission(path, userName, 0)) {
                System.out.println("you have no access to create file or folder");
                return null;
            }
        }
        String [] directories = path.split("/");
        if(!directories[0].equals("root")) {
            System.out.println("invalid path");
            return null;
        }

        ArrayList<Directory> subDirectories = Main.root.subDirectories;
        for(int i=1;i<directories.length-1;){ //directory[1]
            boolean found2 = false;
            for (int j = 0; j < subDirectories.size(); j++) {
                if(directories[i].equals(subDirectories.get(j).getName())){
                    found2 = true;
                    subDirectories = subDirectories.get(j).subDirectories;
                    i++;
                    break;
                }
            }
            if(!found2){
                System.out.println("invalid path");
                return null;
            }
        }
        for (int j = 0; j < subDirectories.size(); j++) {
            if (directories[directories.length-1].equals(subDirectories.get(j).getName())) {
                System.out.println("path already exists");
                return null;
            }
        }
        Directory dir = new Directory();
        dir.setName(directories[directories.length-1]);
        subDirectories.add(dir);
        return dir;
    }
    public boolean deleteFile(String path,String userName){
        if (Main.currentUser != 0) {
            if(!getPermission(path,userName,1)){
                System.out.println("you have no access to delete file or folder");
                return false;
            }
        }

        String [] directories = path.split("/");
        if(!directories[0].equals("root")) {
            System.out.println("invalid path");
            return false;
        }
        ArrayList<Directory> subDirectories = Main.root.subDirectories;
        Directory parent = Main.root;
        for(int i=1;i<directories.length-1;){ //directory[1]
            boolean found2 = false;
            for (int j = 0; j < subDirectories.size(); j++) {
                if(directories[i].equals(subDirectories.get(j).getName())){
                    found2 = true;
                    parent = subDirectories.get(j);
                    subDirectories = subDirectories.get(j).subDirectories;
                    i++;
                    break;
                }
            }
            if(!found2){
                System.out.println("invalid path");
                return false;
            }
        }

        for (int j = 0; j < parent.files.size(); j++) {
            if (directories[directories.length - 1].equals(parent.files.get(j).getName())) { //file exists, free allocation then delete obj
               freeAllocation(parent.files.get(j)); // free allocation in disk
                parent.files.remove(j);
                return true;
            }
        }
        return false;
    }
    public boolean deleteDirectory(String path,String userName){
        if(Main.currentUser!=0){
            if(!getPermission(path,userName,1)){
                System.out.println("you have no access to delete file or folder");
                return false;
            }
        }
        String [] directories = path.split("/");
        if(!directories[0].equals("root")) {
            System.out.println("invalid path");
            return false;
        }

        ArrayList<Directory> subDirectories = Main.root.subDirectories;
        for(int i=1;i<directories.length-1;){ //directory[1]
            boolean found2 = false;
            for (int j = 0; j < subDirectories.size(); j++) {
                if(directories[i].equals(subDirectories.get(j).getName())){
                    found2 = true;
                    subDirectories = subDirectories.get(j).subDirectories;
                    i++;
                    break;
                }
            }
            if(!found2){
                System.out.println("invalid path");
                return false;
            }
        }

        for (int j = 0; j < subDirectories.size(); j++) {
            if(directories[directories.length-1].equals(subDirectories.get(j).getName())) { //i found the intended dir
                deleteFilesInsideADirectory(subDirectories.get(j)); //backtracking for all subtree under this directory
                subDirectories.remove(j);
                return true;
            }
        }
        return false;
    }
    public void deleteFilesInsideADirectory(Directory directory){
        for (int i = 0; i < directory.files.size(); i++) {
            freeAllocation(directory.files.get(i));
        }
        for (int i = 0; i < directory.subDirectories.size(); i++) {
            deleteFilesInsideADirectory(directory.subDirectories.get(i));
        }
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
    public String grantUserPermission(String user_name,String path,String permission){
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
        String [] directories = path.split("/");
        if(!directories[0].equals("root")) {
            return ("invalid path");

        }

        ArrayList<Directory> subDirectories = Main.root.subDirectories;
        for(int i=1;i<directories.length;){ //directory[1]
            boolean found2 = false;
            for (int j = 0; j < subDirectories.size(); j++) {
                if(directories[i].equals(subDirectories.get(j).getName())){
                    found2 = true;
                    subDirectories = subDirectories.get(j).subDirectories;
                    i++;
                    break;
                }
            }
            if(!found2) return "invalid path";
        }
        grant(path,user_name,permission);
        return "permission granted";
    }
    private void grant(String path,String userName,String permission){
        ArrayList<String>file_content = new ArrayList<>();
        try {
            File file = new File("permissions.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line=null;
            while ((line = reader.readLine()) != null){
                file_content.add(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       // String newContent="";
        boolean found = false;
        for (int i = 0; i < file_content.size(); i++) { //for each line, if path exists, add user and permission
            String[] words = file_content.get(i).split(",");
            String newLine = file_content.get(i);
            if(words[0].equals(path)){//for each word
                found = true;
                newLine += (userName+","+permission+",");
            }
        }
        if(!found){ //if path not exist, write new path in the file
            file_content.add(path+","+userName+","+permission+","+"\n");
        }
        try {
            FileWriter writer = new FileWriter("permissions.txt");
            for (int i = 0; i < file_content.size(); i++) {
                writer.write(file_content.get(i)+"\n");
            }
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean getPermission(String path,String userName,int commandType){
        String[] paths = path.split("/");
        if(getPermissionForAPath("root",userName,commandType)) return true;
        for(int i=1;i<paths.length-1;i++) {
            if(getPermissionForAPath(returnPath(paths,i),userName,commandType)) return true;
        }
        return false;
    }
    private String returnPath(String[] mainPath,int index){
        String path = "root";
        for (int i = 1; i <= index; i++) {
            path+="/"+mainPath[i];
        }
        return path;
    }
    public boolean getPermissionForAPath(String newPath,String userName,int commandType){//index of command, 0 for create, 1 for delete

        ArrayList<String>file_content = new ArrayList<>();
        try {
            File file = new File("permissions.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line=null;
            while ((line = reader.readLine()) != null){
                file_content.add(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //String newContent="";
        for (int i = 0; i < file_content.size(); i++) { //for each line, if path exists, add user and permission
            String[] words = file_content.get(i).split(",");
            if(words[0].equals(newPath)){//for each word
                for (int j = 1; j < words.length; j+=2) { //search for user
                    if(words[j].equals(userName)){
                        if(words[j+1].charAt(commandType) == '1') return true;
                    }
                }
            }
        }
        return false;
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

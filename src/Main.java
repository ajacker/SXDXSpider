
public class Main {
    public static void main(String[] args) {
        boolean isRight=false;
        int i=0;
        while(!isRight){
            i++;
            System.out.println("尝试次数："+i);
            isRight=utils.login("201701003085","456852");
            isRight=utils.getClassIMG("/home/ajacker/Desktop/class.jpg",1000,1447);
        }
        System.out.println("课表下载成功!");
    }
}

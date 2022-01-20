public class Main implements Runnable {

    Design design = new Design();

    @Override
    public void run(){
        while(true) {
            design.repaint();
            if(design.reset == false){
                design.gameStatus();
            }
        }
    }

    public static void main(String[] args){
        new Thread(new Main()).start();
    }
}

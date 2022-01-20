import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;

public class Design extends JFrame {

    int space = 1;
    public int mouseX = -100;
    public int mouseY = -100;

    /** Game status */
    public boolean reset = false;
    public boolean status = true;
    public int iconX = 605;
    public int iconY = 5;
    public int iconCenX = iconX + 35;
    public int iconCenY = iconY + 35;

    public boolean win = false;
    public boolean loss = false;

    public boolean flagActive = false;
    public int flagNum = 0;

    public int flagNumX = 190;
    public int flagNumY = 5;

    /** Player will be able to adjust difficulty */
    public int difficulty = 0;
    public int diffX = 5;
    public int diffY = 5;

    public int level = 20;

    /** Timer box */
    public int timerX = 1130;
    public int timerY = 5;
    public int sec = 0;

    /** Flag circle */
    public int flagX = 445;
    public int flagY = 5;
    public int flagCenX = flagX + 35;
    public int flagCenY = flagY + 35;

    /** A tile can either have a mine, show a number, be flagged, or cleared */
    int[][] mines = new int[16][9];
    int[][] prox = new int[16][9];
    boolean[][] flags = new boolean[16][9];
    boolean[][] shown = new boolean[16][9];

    int count = 0;

    Random random = new Random();

    Date time = new Date();

    public Design(){
        /** Customizing the application window */
        Image logo = Toolkit.getDefaultToolkit().getImage("pixil-icon.png");
        this.setIconImage(logo);
        this.setTitle("Minesweeper");
        this.setSize(1286, 829);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        /** Creating the game board */
        Board board = new Board();
        this.setContentPane(board);

        Hover hover = new Hover();
        this.addMouseMotionListener(hover);

        Select select = new Select();
        this.addMouseListener(select);

        createBoard();
    }

    public class Board extends JPanel {
        public void paint(Graphics g){
            /** Window background color */
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0,0,1280,800);

            BufferedImage flag = null;
            try {
                flag = ImageIO.read(new File("pixil-layer-Layer 1.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            /** Creating board grid */
            for(int i = 0; i < 16; i++){
                for(int j = 0; j < 9; j++){
                    g.setColor(Color.gray);

                    if(mines[i][j] == 1){
                        g.setColor(Color.lightGray);
                    }

                    BufferedImage tile = null;
                    try {
                        tile = ImageIO.read(new File("pixil-frame-0.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    g.drawImage(tile,space+i*80 ,space+j*80+80,this);

                    if(shown[i][j] == true){
                        BufferedImage tile3 = null;
                        try {
                            tile3 = ImageIO.read(new File("pixil-layer-Layer 4.png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        g.drawImage(tile3,space+i*80 ,space+j*80+80,this);
                        if(mines[i][j] == 1){
                            g.setColor(Color.red);
                        }
                    }

                    if(mines[i][j] == 1){
                        g.setColor(Color.lightGray);
                    }

                    /** Hovering over a box will change its color until the mouse moves away */
                    if(mouseX >= space+i*80 && mouseX < space+i*80+80-2*space
                        && mouseY >= space+j*80+106 && mouseY < space+j*80+160-2*space && loss == false){
                        BufferedImage tile2 = null;
                        try {
                            tile2 = ImageIO.read(new File("pixil-frame-1.png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        g.drawImage(tile2,space+i*80 ,space+j*80+80,this);
                    }

                    /** Different colors for each number */
                    if(shown[i][j] == true){
                        switch(prox[i][j]){
                            case 1:
                                g.setColor(Color.blue.brighter());
                                break;
                            case 2:
                                g.setColor(Color.green);
                                break;
                            case 3:
                                g.setColor(Color.orange);
                                break;
                            case 4:
                                g.setColor(Color.blue.darker());
                                break;
                            case 5:
                                g.setColor(Color.red);
                                break;
                            case 6:
                                g.setColor(Color.cyan);
                                break;
                            case 8:
                                g.setColor(Color.gray);
                                break;
                            default:
                                g.setColor(Color.black);
                                break;
                        }
                        if(mines[i][j] == 0 && prox[i][j] != 0) {
                            g.setFont(new Font("Arcade", Font.BOLD, 40));
                            g.drawString(Integer.toString(prox[i][j]), i * 80 + 27, j * 80 + 80 + 55);
                        }
                        else if (mines[i][j] == 1){
                            /** Mine */
                            BufferedImage mine = null;
                            try {
                                mine = ImageIO.read(new File("pixil-layer-mine.png"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            g.drawImage(mine,space+i*80 ,space+j*80+80,this);
                            /** Lost */
                            status = false;
                        }
                    }

                    /** Flag tiles */
                    if(flags[i][j] == true){
                        g.drawImage(flag,space+i*80 ,space+j*80+80,this);
                    }

                }
            }

            /** Difficulty Box */
            g.setColor(Color.black);
            g.fillRect(diffX,diffY, 127, 70);
            g.setColor(Color.white);
            g.setFont(new Font("Comic Sans", Font.PLAIN, 57));
            if(difficulty == 0){
                g.drawString("Easy", diffX,diffY+57);
            } if(difficulty == 1){
                g.drawString("Mid", diffX,diffY+57);
            } if(difficulty == 2) {
                g.drawString("Hard", diffX,diffY+57);
            }
            /** Number of flags */
            g.setColor(Color.black);
            g.fillRect(flagNumX, flagNumY, 70, 70);
            g.setColor(Color.white);
            g.setFont(new Font("Comic Sans", Font.PLAIN, 60));
            g.drawString(Integer.toString(flagNum),flagNumX, flagNumY+60);
            /** Icon */
            BufferedImage icon = null;
            try {
                icon = ImageIO.read(new File("pixil-layer-happy.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(icon,iconX,iconY,this);

            if(status == true){
                g.drawImage(icon,iconX,iconY,this);
            } else {
                BufferedImage icon2 = null;
                try {
                    icon2 = ImageIO.read(new File("pixil-layer-sad.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                g.drawImage(icon2,iconX,iconY,this);
            }

            /** Flag */
            g.setColor(Color.blue);
            g.drawOval(flagX, flagY, 70,70);
            g.drawImage(flag,flagX-3 ,flagY-4,this);

            if(flagActive == true){
                g.setColor(Color.green);
                g.drawOval(flagX, flagY, 70, 70);
            }

            /** The timer will stop running when the player wins or loses. */
            g.setColor(Color.black);
            g.fillRect(timerX, timerY, 170, 70);
            if(win == false && loss == false){
                g.setColor(Color.white);
                sec = (int)((new Date().getTime() - time.getTime()) / 1000);
            }
            if(loss == true){
                g.setColor(Color.red);
                reveal();
            }
            if(sec > 999){
                sec = 999;
            }
            g.setFont(new Font("Comic Sans", Font.PLAIN, 80));
            if(sec < 10) {
                g.drawString("00" + Integer.toString(sec), timerX, timerY+67);
            } else if(sec >= 10 && sec < 100){
                g.drawString("0" + Integer.toString(sec), timerX, timerY+67);
            } else {
                g.drawString(Integer.toString(sec), timerX, timerY+67);
            }
        }
    }

    public class Hover implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent mouseEvent) {}

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
        }
    }

    /** Reveal the adjacent non-mine tiles if a blank tile was selected */
    public void floodFill(int x, int y){
        /** Prevent out of bounds errors */
        if(x > 15 || y > 8 || x < 0 || y < 0){ return; }
        if(shown[x][y] == true){ return; }
        if(mines[x][y] == 1){ return; }
        shown[x][y] = true;
        /** Prevent showing any tiles beyond the immediate adjacent numbered-tiles */
        if(prox[x][y] > 0){ return; }
        floodFill(x+1,y);
        floodFill(x-1,y);
        floodFill(x,y-1);
        floodFill(x,y+1);
        return;
    }


    public class Select implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();

            if(tileX() != -1 && tileY() != -1){
//                System.out.println(tileX() +"\n" + tileY() + "\n" + prox[tileX()][tileY()]);
                /** If the player loses, they shouldn't be able to click on any tiles */
                if(loss == false) {
                    if (flagActive == true && shown[tileX()][tileY()] == false) {
                        /** If a tile is unflagged, it can be flagged and vice versa */
                        if (flags[tileX()][tileY()] == false && flagNum > 0) {
                            flags[tileX()][tileY()] = true;
                            flagNum--;
                        } else {
                            if(flags[tileX()][tileY()] == true){
                                flags[tileX()][tileY()] = false;
                                flagNum++;
                            }
                        }
                    } else {
                        /** Prevent the user from showing a tile while it is flagged */
                        if (flags[tileX()][tileY()] == false) {
                            if(shown[tileX()][tileY()] == false && prox[tileX()][tileY()] == 0){
                                floodFill(tileX(), tileY());
                            }
                            shown[tileX()][tileY()] = true;
                        }
                    }
                } else {
                    System.out.println("Reset game");
                }
            }
            if(inDiff() == true){
                inDiff();
                reset();
            }
            if(inFlag() == true){
                if(flagActive == false) {
                    flagActive = true;
                } else {
                    flagActive = false;
                }
            }
            if(inIcon() == true) {
                reset();
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }

    /** Reveals every tile */
    public void reveal(){
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 9; j++){
                shown[i][j] = true;
                flags[i][j] = false;
            }
        }
    }
    /** The following three methods determine if the player has revealed all non-mine tiles to win or has selected a mine
     *      tile which is a loss. */
    public void gameStatus(){
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 9; j++){
                /** If the player has clicked on a tile which is also a mine, they lose */
                if(shown[i][j] == true && mines[i][j] == 1){
                    loss = true;
                }
            }
        }
        /** If all non-mine tiles have been clicked on, the player wins */
        if(tilesShown() >= 144 - mines()){
            win = true;
        }
    }

    public int mines(){
        int mineCount = 0;
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 9; j++){
                if(mines[i][j] == 1){
                    mineCount++;
                }
            }
        }
        return mineCount;
    }

    public int tilesShown(){
        int tiles = 0;
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 9; j++){
                if(shown[i][j] == true){
                    tiles++;
                }
            }
        }
        return tiles;
    }

    /** Detecting if the cursor is over the difficulty box */
    public boolean inDiff(){
        int dis = (int)(Math.abs(mouseX-85) + Math.abs(mouseY-35));
        if(dis < 84 && dis > 5){
            difficulty++;
            if(difficulty > 2){
                difficulty = 0;
            }
            return true;
        }
        return false;
    }

    /** Detecting if cursor is over circular icon by calculating distance from circumference to center*/
    public boolean inIcon(){
        int dis = (int)Math.sqrt(Math.abs(mouseX-iconCenX) * Math.abs(mouseX-iconCenX)
                    + Math.abs(mouseY-iconCenY) * Math.abs(mouseY-iconCenY));
        if(dis < 35){
            return true;
        }
        return false;
    }

    /** Detecting if cursor is over flag icon by calculating distance from circumference to center*/
    public boolean inFlag(){
        int dis = (int)Math.sqrt(Math.abs(mouseX-flagCenX) * Math.abs(mouseX-flagCenX)
                + Math.abs(mouseY-flagCenY) * Math.abs(mouseY-flagCenY));
        if(dis < 35){
            return true;
        }
        return false;
    }

    /** The following two methods determine if the cursor is within the boundaries of a particular tile */
    public int tileX(){
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 9; j++){
                if(mouseX >= space+i*80 && mouseX < i*80+80-space
                        && mouseY >= space+j*80+106 && mouseY < j*80+186-space){
                    return i;
                }
            }
        }
        return -1;
    }

    public int tileY(){
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 9; j++){
                if(mouseX >= space+i*80 && mouseX < i*80+80-space
                        && mouseY >= space+j*80+106 && mouseY < j*80+186-space){
                    return j;
                }
            }
        }
        return -1;
    }

    /** Checks if the box selected is in close proximity to a mine. A clicked on box is the center of
     *      a 9-tiled square */
    public boolean isProx(int aX, int aY, int bX, int bY){
        if((aX - bX < 2) && (aX - bX > -2) && (aY - bY < 2) && (aY - bY > -2) && (mines[bX][bY] == 1)){ return true; }
        return false;
    }

    public void createBoard(){
        if(difficulty == 0){
            level = 10;
        } else if(difficulty == 1){
            level = 20;
        } else {
            level = 40;
        }
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 9; j++){
                /** level% of the board are mines */
                if(random.nextInt(100) < level){
                    mines[i][j] = 1;
                } else {
                    mines[i][j] = 0;
                }
                shown[i][j] = false;
            }
        }
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 9; j++){
                count = 0;
                for(int a = 0; a < 16; a++){
                    for(int b = 0; b < 9; b++){
                        if(!(a == i && b == j)) {
                            if (isProx(i, j, a, b) == true) {
                                count++;
                            }
                        }
                    }
                }
                prox[i][j] = count;
                flags[i][j] = false;
            }
        }
        flagNum = mines();
    }

    public void reset() {
        /** The reset process begins here and will revert to false at the end of the method's scope*/
        reset = true;
        flagActive = false;
        status = true;
        win = false;
        loss = false;
        time = new Date();
        createBoard();
        reset = false;
    }

}


import Records.RecordC;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import scala.collection.mutable.Queue;


import javax.imageio.ImageIO;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Map {

    private final JFrame LGFrame;
    private final BoardPanel boardPanel;
    private int grade [][] =  new int [20][20];
    private int currMap [][] = new int [20][20];
    private final Color COLOR_A  = Color.decode("0x0a9321");
    private final Color COLOR_B  = Color.decode("0x11631f");
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(1000,1000);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(800,700);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private final String imageDir = "art/";//needs to be edited to have image Directory that holds all of the images used
    private RecordC record;


    public Map(RecordC rec){// Map class: makes window calling other classes in this file
        this.LGFrame = new JFrame("LAGI");
        this.LGFrame.setLayout(new BorderLayout());
        this.LGFrame.setSize(OUTER_FRAME_DIMENSION);
        final JMenuBar tableMenuBar = new JMenuBar();
        populateMenuBar(tableMenuBar);
        this.LGFrame.setJMenuBar(tableMenuBar);
        record = rec.deepClone();
        copyArray(record.rec.grids().dequeue());
        //make Record class seriealizable and then deepClone it into a Record object in the map class
        this.boardPanel = new BoardPanel();
        this.LGFrame.add(this.boardPanel, BorderLayout.CENTER);
        // grade = rec.grade();// 2D array holding the grade of each tile on the map for color depiction
        this.LGFrame.setVisible(true);



    }


    private class BoardPanel extends JPanel{//board class
        final List<TilePanel> boardTiles;

        BoardPanel() {//initializes the board (graphical grid)
            super(new GridLayout(20, 20));
            this.boardTiles = new ArrayList<>();
            int x = 0, y = 0;
            for (int i = 0; i < 400; i++) {

                final TilePanel tilePanel = new TilePanel(this, y, x);
                //System.out.println("(" + x + " ," + y + ")");
                this.boardTiles.add(tilePanel);
                add(tilePanel);
                x++;
                if (x == 20) {
                    x = 0;
                    y++;
                }
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        /*
        public void reassign()
        {
            for (int i = 0; i < 400; i++)
            {
                currMap = grids.dequeue();
                //copyA(rec.grids().dequeue());
                this.boardTiles.get(i).assignTileImage(boardPanel);
            }
        }*/
        }

        public void drawBoard(final BoardPanel boardPanel) // Draws board, currently also used to update the board
        {
            removeAll();
            copyArray(record.rec.grids().dequeue());
            for(final TilePanel tilePanel : boardTiles)
            {
                //System.out.print(boardTiles.size());
                tilePanel.drawTile(boardPanel);
                add(tilePanel);
            }
            validate();
        }
    }

    private class TilePanel extends JPanel{//Tile class contained in board objects

        private final int tileX;
        private final int tileY;

        TilePanel(final BoardPanel boardPanel, final int tileX, final int tileY){//initializes tile
            super(new GridBagLayout());
            this.tileX = tileX;
            this.tileY = tileY;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTileImage(boardPanel);
            validate();

            addMouseListener(new MouseListener() {// Listens for click action from each tile
                @Override
                public void mouseClicked(MouseEvent e) {
                    boardPanel.drawBoard(boardPanel);
                }//currently redraws whole board using boardpanel function

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

        }

        public void drawTile(final BoardPanel boardPanel)//draws each tile using other function calls
        {
            assignTileColor();
            assignTileImage(boardPanel);
            validate();
            repaint();
        }

        private void assignTileImage(final BoardPanel boardPanel){//picks an image to draw on tile based on the map number from the 2D array queue from Frame
            this.removeAll();
            //System.out.println(currMap[this.tileX][this.tileY] +" " +this.tileX + " " +this.tileY);

            if(currMap[this.tileX][this.tileY] != 0 || true)
            {
                // System.out.println("X")
                try {

                    final BufferedImage image = ImageIO.read(new File(imageDir + getImageName(currMap[this.tileX][this.tileY])));
                    add(new JLabel(new ImageIcon(image.getScaledInstance(50,50,0))));
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("ERROR READING IMAGE FOR TILE (" + this.tileX + " ,"+ this.tileY + ")" );
                }
            }

        }

        private String getImageName(int i){//returns the image for the respective item in the space (using 2D array from frame)
            //this needs to be edited to return the file name of each image that could be represented in the map i.e 1 = "LGImageName.gif"
            //System.out.print(i);
            if (i == -1) {
                //System.out.println("F");
                return "wall.PNG";

            }
            else if (i == 1) {
                //System.out.println("F");
                return "lg3.PNG";

            }
            else if(i == 2)
            {
                return "portal4.png";
            }
            else if(i == 3)//Door out of transport
            {
                return "cave4.png";
            }
            else if(i == 4)
            {
                return "burger3.png";
            }
            else if(i == 5)
            {
                return "snake2.png";
            }
            else if(i == 7)//Door to transport
            {
                return "cave4.png";
            }
            else//This is used to overwrite the items remaining in the grid from the previous iteration of of the grid
            {
                return "Blank.png";
            }
        }

        private void assignTileColor() {//Needs to be modified to reflect grades of the map rather than just a checker board green
            if(tileX % 2 == 0){
                setBackground(this.tileY % 2 == 0 ? COLOR_A : COLOR_B);
            }
            else if(tileX % 2 != 0){
                setBackground(this.tileY % 2 == 0 ? COLOR_B : COLOR_A);            }
        }


    }

    private void populateMenuBar(final JMenuBar tableMenuBar){
        tableMenuBar.add(createFileMenu());
    }//Just makes a menu bar, could actually contain things in it at some point

    private JMenu createFileMenu(){
        final JMenu fileMenu  = new JMenu("File");

        final JMenuItem NextSlide = new JMenuItem("Next Move");
        NextSlide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Next grid iteration");
            }
        });
        fileMenu.add(NextSlide);
        return fileMenu;
    }

    public void copyArray(int [][] x) //Copies 2D array avoiding referencing error
    {
        int yaes = 0;
        for (int i = 0; i < 20; i++)
        {
            for (int j = 0; j < 20; j++)
            {
                //System.out.print(x[i][j]+",");
                //if (yaes  == 19){System.out.println(""); yaes =0;}
                //else {yaes++;}
                currMap[i][j] = x[i][j];
            }
        }
        System.out.println("n");
    }
}


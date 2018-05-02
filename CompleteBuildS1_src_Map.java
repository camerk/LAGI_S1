
import Records.RecordC;
/*
Last Edited by Cameron Kane 5/2/18
*/

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;


public class Map {

    private final JFrame LGFrame;
    private final BoardPanel boardPanel;
    private int grade [][] =  new int [20][20];
    private int currMap [][] = new int [20][20];
    private int prevMap [][] = new int [20][20];
    private final Color COLOR_A  = Color.decode("0x0a9321");
    private final Color COLOR_B  = Color.decode("0x11631f");
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(1000,1000);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(800,700);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private final String imageDir = "art/";//needs to be edited to have image Directory that holds all of the images used
    private RecordC record;
    private boolean isFirst = true;// tells if the map is on the first iteration or not
    int stepCount = 0;
    int totalStepCount = 0;
    String tod = "Day";
    String temp;
    JLabel currStepLabel = new JLabel("Current Step: " + stepCount);
    JLabel totalStepLabel = new JLabel("Total Steps: " + totalStepCount);
    JLabel TODLabel = new JLabel(" Time of Day: " + tod);
    boolean isEaten = false;
    boolean isFound = false;
    boolean isDead = false;
    boolean isDay = true;
    String ResultString = "Little Guy met the end of his Road!";

    public Map(RecordC rec){// Map class: makes window calling other classes in this file
        this.LGFrame = new JFrame("LAGI");
        this.LGFrame.setLayout(new BorderLayout());
        this.LGFrame.setSize(OUTER_FRAME_DIMENSION);
        final JMenuBar tableMenuBar = new JMenuBar();
        populateMenuBar(tableMenuBar);
        this.LGFrame.setJMenuBar(tableMenuBar);
        record = rec.deepClone();
        nextGrid(record.rec.grids().dequeue());
        //make Record class seriealizable and then deepClone it into a Record object in the map class
        this.boardPanel = new BoardPanel();
        this.LGFrame.add(this.boardPanel, BorderLayout.CENTER);
        totalStepLabel.setText( "   Total Steps: " + record.rec.totalSteps1());
        isEaten = (boolean) record.rec.isEaten1().dequeue();
        isFound = (boolean) record.rec.isfounds1().dequeue();
        isDead = (boolean) record.rec.isDead1().dequeue();
        isDay = (boolean) record.rec.isDay1().dequeue();
        setTOD();
        this.LGFrame.setVisible(true);



    }

    private void setTOD() {
        if (isDay == true){
            tod = " Day time ";
        }
        else {
            tod = " Night time";
        }
        TODLabel.setText(" Time of Day: " + tod);
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
        }
        public void updateBoard(final BoardPanel boardPanel) // updates board //Will exit when Queue is empty
        {
            if (record.rec.grids().isEmpty())
            {
                if (isEaten == true)
                {
                    ResultString = "Little Guy was Eaten!";
                }
                else if (isFound == true)
                {
                    ResultString = "Little Guy Found the Portal! He survived and won!";
                }
                else if (isDead == true)
                {
                    ResultString = "Little guy Died of Hunger :/";
                }
                JOptionPane.showMessageDialog(null, ResultString, "Simulation Over",
                        JOptionPane.INFORMATION_MESSAGE);
                exit(0);
            }
            nextGrid(record.rec.grids().dequeue());

            isEaten = (boolean) record.rec.isEaten1().dequeue();
            isFound = (boolean) record.rec.isfounds1().dequeue();
            isDead = (boolean) record.rec.isDead1().dequeue();
            isDay = (boolean) record.rec.isDay1().dequeue();
            setTOD();

            stepCount++;
            currStepLabel.setText("Current Step: "+ stepCount);
            for(final TilePanel tilePanel : boardTiles)
            {
                tilePanel.updateTile(boardPanel);
            }
            validate();
        }


        public void drawBoard(final BoardPanel boardPanel) // Draws board, currently also used to update the board
        {
            nextGrid(record.rec.grids().dequeue());
            for(final TilePanel tilePanel : boardTiles)
            {
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
                    System.out.println("*Tile Mouse Click*");//Click flag
                    boardPanel.updateBoard(boardPanel);
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

        public void updateTile(final BoardPanel boardPanel)//updates each tile using other function calls
        {
            System.out.println("Update");//Function flag
            updateTileImage(boardPanel);
            validate();
            repaint();
        }

        private void updateTileImage(final BoardPanel boardPanel){//picks an image to draw on tile based on the map number from the 2D array queue from Frame
            System.out.print("Update_Tile_Image**");//Function call Flag
            if (currMap[this.tileX][this.tileY] == prevMap[this.tileX][this.tileY])// if the new gird is the same as the old, no need to update image on this tile
            {
                return;
            }

            this.removeAll();//Remove old image (removes everything in tile except color)

            if(currMap[this.tileX][this.tileY] != 0)// if the tile space is occupied by not zero (not just an empty space)
            {
                try {

                    final BufferedImage image = ImageIO.read(new File(imageDir + getImageName(currMap[this.tileX][this.tileY])));// appends file location to file name
                    add(new JLabel(new ImageIcon(image.getScaledInstance(50,50,0))));//adds the scaled image to the tile
                } catch (IOException e){//catches read error of image for tile
                    e.printStackTrace();
                    System.out.println("ERROR READING IMAGE FOR TILE (" + this.tileX + " ,"+ this.tileY + ")" );
                }
            }

        }

        private void assignTileImage(final BoardPanel boardPanel){//picks an image to draw on tile based on the map number from the 2D array queue from Frame
            //System.out.println("ATI");// function flag

            if(currMap[this.tileX][this.tileY] != 0)
            {
                try {// see update file image for in depth comments on the following
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
            if (i == -1) {
                return "wall.PNG";
            }
            else if (i == 1) {
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
            else//This is just a catch all
            {
                return "Blank.png";
            }
        }

        private void assignTileColor() {//Needs to be modified to reflect elevation of the map rather than just a checker board green
            if(tileX % 2 == 0){
                setBackground(this.tileY % 2 == 0 ? COLOR_A : COLOR_B);
            }
            else if(tileX % 2 != 0){
                setBackground(this.tileY % 2 == 0 ? COLOR_B : COLOR_A);            }
        }


    }

    private void populateMenuBar(final JMenuBar tableMenuBar){
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(currStepLabel);
        tableMenuBar.add(totalStepLabel);
        tableMenuBar.add(TODLabel);
    }//Just makes a menu bar, could actually contain things in it at some point

    private JMenu createFileMenu(){
        final JMenu OpMenu  = new JMenu("Options");

        final JMenuItem NextSlide = new JMenuItem("Exit");
        NextSlide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit(0);
            }
        });
        OpMenu.add(NextSlide);
        return OpMenu;
    }

    public void nextGrid(int [][] x) //Copies 2D array avoiding referencing error
    {
        for (int i = 0; i < 20; i++)
        {
            for (int j = 0; j < 20; j++)
            {
                if (!isFirst)// passes curr map to prev map before copying next iteration into curr map
                {
                    prevMap[i][j] = currMap[i][j];
                }
                currMap[i][j] = x[i][j];
            }
        }
        isFirst = false;
        System.out.println("NG");//Function flag
    }

    public void exitCatch()
    {

    }
}


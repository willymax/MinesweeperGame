import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class BoardPanel extends JPanel implements MouseListener {

    private final int NUM_OF_ICONS = 13;
    private final int TOTAL_NUM_OF_MINES = 40;


    //constants holding predefined values of tiles
    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int CELL_WITH_MINE = 9;
    private final int EMPTY_CELL = 0;

    //this will represent a cell with mine which is is hidden
    private final int COVERED_MINE_CELL = CELL_WITH_MINE + COVER_FOR_CELL;
    //this will represent a cell with mine which is is hidden
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int SHOW_MINE = 9;
    private final int SHOW_COVER = 10;
    private final int MARK_MINE_SELECTION = 11;
    private final int MARK_WRONG_MINE_SELECTION = 12;

    //16x16 grid of tiles
    private final int NUM_ROW_TILES = 16;
    private final int NUM_COL_TILES = 16;
    private final int TOTAL_NUMBER_OF_CELLS = NUM_ROW_TILES * NUM_COL_TILES;

    //the cells will be squares so same width and height
    private final int CELL_WIDTH_HEIGHT = 15;
    private final int BOARD_WIDTH = NUM_COL_TILES * CELL_WIDTH_HEIGHT + 1;
    private final int BOARD_HEIGHT = NUM_ROW_TILES * CELL_WIDTH_HEIGHT + 1;

    private final JLabel statusBar;
    private final JLabel timerLabel;

    private int[] boardOfCells;
    private boolean continueWithGame;
    private boolean newGame;
    private int totalMinesLeft;
    private Image[] icons;
    private Timer timer;

    public BoardPanel(JLabel statusBar, JLabel timerLabel) {
        this.statusBar = statusBar;
        this.timerLabel = timerLabel;
        icons = new Image[NUM_OF_ICONS];
        //iterate through the icons and add them to the items array
        for (int i = 0; i < NUM_OF_ICONS; i++) {
            String path = "icons/" + i + ".png";
            icons[i] = (new ImageIcon(path)).getImage();
        }
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        //add mouse clicked listener to the Panel
        addMouseListener(this);
        createNewGame();
    }

    /**
     * This method resets the game to start
     */
    public void createNewGame() {
        Random random = new Random();
        //
        totalMinesLeft = TOTAL_NUM_OF_MINES;
        continueWithGame = true;
        newGame = true;

        timerLabel.setText("Time remaining: 10000");

        //set all cells to their default values
        boardOfCells = new int[TOTAL_NUMBER_OF_CELLS];

        //set all the cells as cover cells
        for (int i = 0; i < TOTAL_NUMBER_OF_CELLS; i++) {
            //every cell has a cover at the start
            boardOfCells[i] = COVER_FOR_CELL;
        }

        //display the number of hidden mines left
        statusBar.setText(Integer.toString(totalMinesLeft));

        //to track the number of mines added so far
        int minesAdded = 0;

        int currentCell;

        //fill the mines at random cells one by one
        while (minesAdded < TOTAL_NUM_OF_MINES) {
            //generate a random cell position between 0 and TOTAL_NUMBER_OF_CELLS
            int position = (int) (TOTAL_NUMBER_OF_CELLS * random.nextDouble());

            //check if there is a cell in the random position selected
            if ((position < TOTAL_NUMBER_OF_CELLS) && (boardOfCells[position] != COVERED_MINE_CELL)) {
                //set the cell to have a mine
                boardOfCells[position] = COVERED_MINE_CELL;

                //calculate the column of the current cell on the board
                int currentColumn = position % NUM_COL_TILES;
                minesAdded++;
                if (currentColumn > 0) {//inspect the neighboring cells on the left
                    //check the cell on the left
                    currentCell = position - 1;
                    if (currentCell >= 0) {
                        if (boardOfCells[currentCell] != COVERED_MINE_CELL) {
                            boardOfCells[currentCell] += 1;
                        }
                    }
                    //check the cell at the diagonal top-left
                    currentCell = position - 1 - NUM_COL_TILES;
                    if (currentCell >= 0) {
                        if (boardOfCells[currentCell] != COVERED_MINE_CELL) {
                            boardOfCells[currentCell] += 1;
                        }
                    }
                    //check the cell at the diagonal bottom-left
                    currentCell = position - 1 + NUM_COL_TILES;
                    if (currentCell < TOTAL_NUMBER_OF_CELLS) {
                        if (boardOfCells[currentCell] != COVERED_MINE_CELL) {
                            boardOfCells[currentCell] += 1;
                        }
                    }
                }
                //check the cell at the top
                currentCell = position - NUM_COL_TILES;
                if (currentCell >= 0) {
                    if (boardOfCells[currentCell] != COVERED_MINE_CELL) {
                        boardOfCells[currentCell] += 1;
                    }
                }
                //check the cell at the bottom
                currentCell = position + NUM_COL_TILES;
                if (currentCell < TOTAL_NUMBER_OF_CELLS) {
                    if (boardOfCells[currentCell] != COVERED_MINE_CELL) {
                        boardOfCells[currentCell] += 1;
                    }
                }
                if (currentColumn < (NUM_COL_TILES - 1)) {//inspect the cell neighboring on the right
                    //check the cell at the right
                    currentCell = position + 1;
                    if (currentCell < TOTAL_NUMBER_OF_CELLS) {
                        if (boardOfCells[currentCell] != COVERED_MINE_CELL) {
                            boardOfCells[currentCell] += 1;
                        }
                    }

                    //check the cell at the diagonal top right
                    currentCell = position - NUM_COL_TILES + 1;
                    if (currentCell >= 0) {
                        if (boardOfCells[currentCell] != COVERED_MINE_CELL) {
                            boardOfCells[currentCell] += 1;
                        }
                    }
                    //check the cell at the diagonal bottom right
                    currentCell = position + NUM_COL_TILES + 1;
                    if (currentCell < TOTAL_NUMBER_OF_CELLS) {
                        if (boardOfCells[currentCell] != COVERED_MINE_CELL) {
                            boardOfCells[currentCell] += 1;
                        }
                    }
                }
            }
        }
    }

    public void resetGame() {
        stopTimer();
        createNewGame();
        repaint();
    }

    /**
     * this is a recursive method that reveals empty tiles adjacent to the tile passed until it comes across tiles with an adjacent mine
     *
     * @param tilePosition position of a tile with no adjacent mines
     */
    private void revealEmptyTiles(int tilePosition) {
        //find the column of the empty tile the position
        int currentTileColumn = tilePosition % NUM_COL_TILES;
        int currentCell;
        if (currentTileColumn > 0) {
            //check the cell at the left
            currentCell = tilePosition - 1;
            if (currentCell >= 0) {
                if (boardOfCells[currentCell] > CELL_WITH_MINE) {
                    boardOfCells[currentCell] -= COVER_FOR_CELL;
                    if (boardOfCells[currentCell] == EMPTY_CELL) {
                        revealEmptyTiles(currentCell);
                    }
                }
            }

            //top left diagonal
            currentCell = tilePosition - NUM_COL_TILES - 1;
            if (currentCell >= 0) {
                if (boardOfCells[currentCell] > CELL_WITH_MINE) {//check whether has mine else remove cover
                    //remove the cover from the cell
                    boardOfCells[currentCell] -= COVER_FOR_CELL;
                    //check if empty
                    if (boardOfCells[currentCell] == EMPTY_CELL) {
                        //reveal the empty tiles around it
                        revealEmptyTiles(currentCell);
                    }
                }
            }

            //diagonal bottom left
            currentCell = tilePosition + NUM_COL_TILES - 1;
            if (currentCell < TOTAL_NUMBER_OF_CELLS) {//check whether has mine else remove cover
                if (boardOfCells[currentCell] > CELL_WITH_MINE) {
                    boardOfCells[currentCell] -= COVER_FOR_CELL;
                    if (boardOfCells[currentCell] == EMPTY_CELL) {
                        revealEmptyTiles(currentCell);
                    }
                }
            }
        }

        //cell at the top
        currentCell = tilePosition - NUM_COL_TILES;
        if (currentCell >= 0) {
            if (boardOfCells[currentCell] > CELL_WITH_MINE) {//check whether has mine else remove cover
                boardOfCells[currentCell] -= COVER_FOR_CELL;
                if (boardOfCells[currentCell] == EMPTY_CELL) {
                    revealEmptyTiles(currentCell);
                }
            }
        }

        //cell at the bottom
        currentCell = tilePosition + NUM_COL_TILES;
        if (currentCell < TOTAL_NUMBER_OF_CELLS) {
            if (boardOfCells[currentCell] > CELL_WITH_MINE) {//check whether has mine else remove cover
                boardOfCells[currentCell] -= COVER_FOR_CELL;
                if (boardOfCells[currentCell] == EMPTY_CELL) {
                    revealEmptyTiles(currentCell);
                }
            }
        }

        if (currentTileColumn < (NUM_COL_TILES - 1)) {
            //cell at the right
            currentCell = tilePosition + 1;
            if (currentCell < TOTAL_NUMBER_OF_CELLS) {//check whether has mine else remove cover
                if (boardOfCells[currentCell] > CELL_WITH_MINE) {
                    boardOfCells[currentCell] -= COVER_FOR_CELL;
                    if (boardOfCells[currentCell] == EMPTY_CELL) {
                        revealEmptyTiles(currentCell);
                    }
                }
            }

            //cell at top right diagonal
            currentCell = tilePosition - NUM_COL_TILES + 1;
            if (currentCell >= 0) {
                if (boardOfCells[currentCell] > CELL_WITH_MINE) {//check whether has mine else remove cover
                    boardOfCells[currentCell] -= COVER_FOR_CELL;
                    if (boardOfCells[currentCell] == EMPTY_CELL) {
                        //reveal empty cells around it
                        revealEmptyTiles(currentCell);
                    }
                }
            }
            //cell at bottom right diagonal
            currentCell = tilePosition + NUM_COL_TILES + 1;
            if (currentCell < TOTAL_NUMBER_OF_CELLS) {
                if (boardOfCells[currentCell] > CELL_WITH_MINE) {//check whether has mine else remove cover
                    boardOfCells[currentCell] -= COVER_FOR_CELL;
                    if (boardOfCells[currentCell] == EMPTY_CELL) {
                        revealEmptyTiles(currentCell);
                    }
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        int uncoveredMinesCount = 0;
        //display the icons representing the cells depending on their values
        for (int row = 0; row < NUM_ROW_TILES; row++) {
            for (int col = 0; col < NUM_COL_TILES; col++) {
                //get the value of the cell at the position row and col
                int currentCellIconValue = boardOfCells[(row * NUM_COL_TILES) + col];
                //check if the game is over
                if (continueWithGame && currentCellIconValue == CELL_WITH_MINE) {
                    continueWithGame = false;
                }
                if (!continueWithGame) {//if game is over
                    if (currentCellIconValue == COVERED_MINE_CELL) {//show mine
                        currentCellIconValue = SHOW_MINE;
                    } else if (currentCellIconValue == MARKED_MINE_CELL) {//mine selected
                        currentCellIconValue = MARK_MINE_SELECTION;
                    } else if (currentCellIconValue > COVERED_MINE_CELL) {//
                        currentCellIconValue = MARK_WRONG_MINE_SELECTION;
                    } else if (currentCellIconValue > CELL_WITH_MINE) {//mine not revealed
                        currentCellIconValue = SHOW_COVER;
                    }
                } else {
                    if (currentCellIconValue > COVERED_MINE_CELL) {//show the cell as selected with mine
                        currentCellIconValue = MARK_MINE_SELECTION;
                    } else if (currentCellIconValue > CELL_WITH_MINE) {//add cover
                        currentCellIconValue = SHOW_COVER;
                        uncoveredMinesCount++;
                    }
                }
                //draw the cell icons
                int x = (col * CELL_WIDTH_HEIGHT);
                int y = (row * CELL_WIDTH_HEIGHT);
                g.drawImage(icons[currentCellIconValue], x, y, this);
            }
        }

        if (continueWithGame && uncoveredMinesCount == 0) {
            continueWithGame = false;
            stopTimer();
            statusBar.setText("You won");
        } else if (!continueWithGame) {
            stopTimer();
            statusBar.setText("Game lost");
        }
    }

    /**
     * This method creates a new Timer object and starts counting from the seconds passed down to zero
     * @param startSeconds the time to start counting
     */
    private void startTimer(int startSeconds) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        int delay = 1000;
        int period = 1000;
        final int[] count = {startSeconds};
        timer.schedule(new TimerTask() {
            public void run() {
                count[0]--;
                timerLabel.setText("Time Remaining: " + count[0]);
                checkTime(count[0]);
            }
        }, delay, period);
    }

    /**
     * This method check if the time has elapse
     *
     * @param currentTimeInSec the current time in the game
     */
    private void checkTime(int currentTimeInSec) {
        if (currentTimeInSec == 1000) {
            stopTimer();
            //end game
            continueWithGame = true;
            repaint();
            statusBar.setText("Timeout!");
            createNewGame();
        }
    }

    /**
     * This method terminates the timer and ends the game
     */
    private void stopTimer() {
        if (timer != null) {
            //stop timer
            timer.cancel();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (newGame) {//check if it's new game and start timer
            newGame = false;
            startTimer(1000);
        }
        //get the position of the mouse
        int x = e.getX();
        int y = e.getY();

        //the position coordinates of the cell clicked
        int cellRow = y / CELL_WIDTH_HEIGHT;
        int cellColumn = x / CELL_WIDTH_HEIGHT;

        boolean redrawBoard = false;

        if (!continueWithGame) {
            createNewGame();
            repaint();
            newGame = false;
            startTimer(1000);
        }

        //check if the mouse was clicked with the game board
        if ((y < NUM_ROW_TILES * CELL_WIDTH_HEIGHT) && (x < NUM_COL_TILES * CELL_WIDTH_HEIGHT)) {
            if (SwingUtilities.isLeftMouseButton(e)) {//LeftMouseButton
                int currentCellIndex = (cellRow * NUM_COL_TILES) + cellColumn;
                if (boardOfCells[currentCellIndex] > COVERED_MINE_CELL) {
                    return;
                }
                if ((boardOfCells[currentCellIndex] > CELL_WITH_MINE) && (boardOfCells[currentCellIndex] < MARKED_MINE_CELL)) {
                    boardOfCells[currentCellIndex] -= COVER_FOR_CELL;
                    redrawBoard = true;
                    if (boardOfCells[currentCellIndex] == CELL_WITH_MINE) {//left clicked on a cell with unrevealed mine
                        continueWithGame = false;
                    }
                    if (boardOfCells[currentCellIndex] == EMPTY_CELL) {
                        revealEmptyTiles(currentCellIndex);
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)){//RightMouseButton
                int currentCellIndex = (cellRow * NUM_COL_TILES) + cellColumn;
                if (boardOfCells[currentCellIndex] > CELL_WITH_MINE) {
                    redrawBoard = true;
                    if (boardOfCells[currentCellIndex] <= COVERED_MINE_CELL) {
                        if (totalMinesLeft > 0) {
                            totalMinesLeft--;
                            boardOfCells[currentCellIndex] += MARK_FOR_CELL;
                            statusBar.setText(Integer.toString(totalMinesLeft));
                        } else {
                            statusBar.setText("No marks left");
                        }
                    } else {
                        totalMinesLeft++;
                        boardOfCells[currentCellIndex] -= MARK_FOR_CELL;
                        statusBar.setText(Integer.toString(totalMinesLeft));
                    }
                }
            }
            if (redrawBoard) {
                repaint();
            }
        }
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
}
// Maddie and Saanvi's Version Assignment 9

/*
 * Welcome to our Game!
 * Please play on a Microsoft for full aspects!
 * make sure to grab our custom made Shrek themed graphics!
 * please be kind while grading! some tests may not work completely, 
 * but do function correctly when implemented.
 * Have a great time!
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;

import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;




// To represent any constants in the game board
interface IConstants {

  int CARDWIDTH = 100;
  int CARDHEIGHT = 148;
  int GRIDWIDTH = 120;
  int GRIDHEIGHT = 160;
  int SCREENWIDTH = 13 * GRIDWIDTH;
  int SCREENHEIGHT = 4 * GRIDHEIGHT;
  WorldScene WS = new WorldScene(SCREENWIDTH, SCREENHEIGHT + 100);

  ArrayList<String> SUITS = new ArrayList<String>(Arrays.asList("♣", "♦", "♥", "♠"));
  // all possible suits
  ArrayList<String> RANKS = new ArrayList<String>(Arrays.asList("A", "2", "3", "4", "5", "6",
      "7", "8", "9", "10", "J", "Q", "K")); // all possible ranks
  ArrayList<Card> flippedList = new ArrayList<Card>();

}

// To represent the current Concentration Game
class ConcentrationWorld extends World implements IConstants {
  Deck deck;
  Random rand;
  int score;
  WorldImage imgScore;
  int timer;

  // Automatically creates a world with all 52 of the cards in order - NOT RANDOMIZED
  ConcentrationWorld() {
    this.deck = new Deck(new ArrayList<Card>());
    this.deck.fullDeck();
    this.rand = new Random();
    this.score = 26;
    this.imgScore = new TextImage("Score: " + this.score, 30, Color.GREEN);
    this.timer = 0;

    this.shuffleDeck(this.rand);
  }

  // construct with specific seed
  ConcentrationWorld(Random rand) {
    this.deck = new Deck(new ArrayList<Card>());
    this.deck.fullDeck();
    this.rand = rand;

    this.shuffleDeck(this.rand);
  }

  // construct for testing
  ConcentrationWorld(Deck deck) {
    this.deck = deck;
    this.rand = new Random();
  }

  // draw the current game
  public WorldScene makeScene() {
    WorldScene ws = new WorldScene(SCREENWIDTH, SCREENHEIGHT + 100);
    ws.placeImageXY(new TextImage("Score: " + this.score, 30, Color.GREEN),
        SCREENWIDTH / 2, SCREENHEIGHT + 50);
    ws.placeImageXY(new TextImage("Time: " + this.time(), 30, Color.GREEN), // change to shrek green
        SCREENWIDTH / 3, SCREENHEIGHT + 50);
    return this.deck.draw(ws);
  }

  // on each tick what should we do
  // check if any cards match and remove from screen
  // increment time

  // EFFECT: On tick update scene and time 
  public void onTick() { 
    this.timer++;
    String currTime = time();

    if (this.deck.isEmpty()) {
      this.endOfWorld("You Won! Score: " + this.score);
    }
    else if (flippedList.size() > 1) {
      this.deck.removeMatch().draw(WS); // put score subtract in removeMatch 
      this.score = this.deck.size() / 2;
    }
    return;
  }

  // return Final scene
  public WorldScene lastScene(String s) {
    WorldScene ws = new WorldScene(SCREENWIDTH, SCREENHEIGHT + 100);
    ws.placeImageXY(new TextImage("You Won!", 40, Color.BLACK),
        SCREENWIDTH / 2, SCREENHEIGHT / 2);
    ws.placeImageXY(new TextImage("Great Job Concentrating!", 30, Color.BLACK),
        SCREENWIDTH / 2, 3 * SCREENHEIGHT / 4);
    ws.placeImageXY(new TextImage("Total Score: " + this.score, 30, Color.BLACK),
        2 * SCREENWIDTH / 3, SCREENHEIGHT);
    ws.placeImageXY(new TextImage("Total Time: " + this.time(), 30, Color.BLACK), SCREENWIDTH / 3,
        SCREENHEIGHT);
    return ws;
  }

  // EFFECT: reset on r press
  @Override
  public void onKeyEvent(String key) {
    if (key.equals("r")) {    // reset board with full set at new randomized positions
      this.deck = new ConcentrationWorld().deck; 
      this.deck.draw(WS);
      this.score = 26;
      this.updateImgScore();
      this.timer = 0;
    }
    return;
  }

  // EFFECT: flip any card where pos is within its bounds
  @Override
  public void onMouseClicked(Posn pos) {
    this.deck.findAndFlip(pos);
  }


  //EFFECT: Shuffle the deck of cards
  public void shuffleDeck(Random rand) {
    this.deck = new Deck();
    this.deck.fullDeck();
    Deck addto = new Deck();
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 13; j++) {
        int index = this.rand.nextInt(this.deck.size());
        Card randCard = this.deck.get(index);

        randCard.adjust(GRIDWIDTH * j + GRIDWIDTH / 2, GRIDHEIGHT * i + GRIDHEIGHT / 2);

        this.deck.remove(index);
        addto.add(randCard);
      }
    }
    this.deck = addto;
  }

  // EFFECT: update the text representing the score
  public void updateImgScore() {
    this.imgScore = new TextImage("Score: " + this.score, 30, Color.GREEN);
  }

  //returns a string that is a timer of minutes and seconds 
  public String time() {
    int minutes = this.timer / 60; 
    int seconds = this.timer % 60;

    String stringMins = String.format("%02d", minutes); 
    String stringSecs = String.format("%02d", seconds);
    // "%02d" converts from num to string w two places

    return stringMins + ":" + stringSecs;

  }

}


// represents a deck of cards (0 - 52 inc)

class Deck implements IConstants {
  ArrayList<Card> deck;

  Deck(ArrayList<Card> deck) {
    this.deck = deck;
  }

  // create new empty deck
  Deck() {
    this.deck = new ArrayList<Card>();
  }


  // is this deck empty?
  public boolean isEmpty() {
    return this.deck.isEmpty();
  }


  // get Card at the index
  public Card get(int index) {
    return this.deck.get(index);
  }

  // get size of deck
  public int size() {
    return this.deck.size();
  }

  // EFFECT: Add to current deck
  public void add(Card e) {
    this.deck.add(e);
  }

  // EFFECT: Remove from current deck
  public void remove(int index) {
    this.deck.remove(index);
  }

  // draws the cards in this deck
  public WorldScene draw(WorldScene ws) {
    for (Card c : this.deck) {
      c.draw(ws);
    }
    return ws;
  }

  // EFFECT: finds the correct card that is clicked on
  public void findAndFlip(Posn pos) {
    for (Card c : this.deck) { 
      // flip only if position is found and card is face down (prevents flipping open cards
      // --> no more spam click bug) 
      if (c.find(pos) && !c.flipped) { // if the posX is within the origin point to width and
        // posY is within the origin point to height
        c.flip(); 
        flippedList.add(c);
      }
    }
    return;
  }



  // remove any cards that have been flipped
  public Deck removeMatch() {

    for (int i = 0; i + 1 < flippedList.size(); i++) {
      if (flippedList.get(i).cardsSame(flippedList.get(i + 1))) { // if equal, remove from deck
        this.deck.remove(flippedList.get(i + 1));
        this.deck.remove(flippedList.get(i));
        this.flippedList.remove(i + 1);
        this.flippedList.remove(i);
        //this.score = this.score - 1;

      }
      else {
        this.flippedList.remove(i + 1);
        this.flippedList.remove(i);
      }
    }

    return this;
  } 


  // EFFECT: creates a full set of cards and adds into a deck 
  public void fullDeck() {
    for (int i = 0; i < SUITS.size(); i++) {
      for (int j = 0; j < RANKS.size(); j++) {
        this.deck.add(new Card(RANKS.get(j), SUITS.get(i)));
      }
    }
  }



}



// represents a singular card

// rank, suite, and if it has been flipped

class Card implements IConstants {
  String rank;
  String suit;
  boolean flipped; // true if face up
  Posn posn;  // where is the Card

  // Full constructor
  Card(String rank, String suit, boolean flipped, Posn posn) {
    this.rank = rank;
    this.suit = suit;
    this.flipped = flipped;
    this.posn = posn;
  }

  // automatic constructor
  Card(String rank, String suit) {
    this.rank = rank;
    this.suit = suit;
    this.flipped = false;
    this.posn = new Posn(0,0);
  }

  // EFFECT: adjust x and y to input
  public void adjust(int j, int i) {
    this.posn = new Posn(j, i);
  }




  // Draws a card based on its suit and value
  // this requires the pngs specific to the program
  // please ensure you have downloaded the shrek deck
  public WorldImage drawCard() {
    if (!this.flipped) {
      return new OverlayImage(
          new FromFileImage(
              "src/BOCShrek.png"), new RectangleImage(
                  GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE));
    }

    else {
      return new OverlayImage(new FromFileImage("src/" + this.rank + this.suit + ".png"),
          new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE));
    }
  }

  // EFFECT: drawing a card with the probided WorldScene
  public void draw(WorldScene ws) {
    ws.placeImageXY(this.drawCard(), this.posn.x, this.posn.y);
  }

  // EFFECT: flip card to other direction
  public void flip() {
    this.flipped = !(this.flipped);
  } 


  // Is the given Posn within Card's bounds
  public boolean find(Posn pos) {

    return pos.x >= this.posn.x - GRIDWIDTH / 2
        && pos.x <= this.posn.x + GRIDWIDTH / 2
        && pos.y >= this.posn.y - GRIDHEIGHT / 2
        && pos.y <= this.posn.y + GRIDHEIGHT / 2; 
  }

  // on tick handler that checks if cards are the same
  // On hard mode: cards must be same rank and same color
  public boolean cardsSame(Card comp) {

    if ((this.rank.equals(comp.rank))  
        // these make the game more difficult and check for colors being equal
        && (((this.suit.equals("♦") || this.suit.equals("♥")) 
            && (comp.suit.equals("♦") || comp.suit.equals("♥")))
            || ((this.suit.equals("♣") || this.suit.equals("♠")) 
                && (comp.suit.equals("♣") || comp.suit.equals("♠"))))) {
      this.flipped = true;
      comp.flipped = true;
      return true;
    }

    else {  // flips back over any same cards
      this.flipped = false;
      comp.flipped = false;
      return false;
    }
  }


}

// Examples for Concentration Game Testing
class ExamplesConcentration implements IConstants {

  Card oneH;
  Card twoH;
  Card threeH;
  Card fourH;
  Card fiveH;
  Card sixH;

  Card foneH;
  Card ftwoH;

  Card twoSpade = new Card("2", "♠");

  ArrayList<Card> firstrow;
  ArrayList<Card> mt;
  ArrayList<Card> listtwo;
  ArrayList<Card> mixedList;

  Deck empty;
  Deck mixedDeck;

  ConcentrationWorld cwone;
  ConcentrationWorld cwtwo;
  ConcentrationWorld cwthree;

  void initData() {
    // cards
    this.oneH = new Card("A", "♥");
    this.twoH = new Card("2", "♥");
    this.threeH = new Card("3", "♥");
    this.fourH = new Card("4", "♥");
    this.fiveH = new Card("5", "♥");
    this.sixH = new Card("6", "♥");

    // flipped cards

    this.foneH = new Card("A", "♥", false, new Posn(10, 35));
    this.ftwoH = new Card("2", "♥", true, new Posn(15, 21));

    // lists of cards

    this.firstrow = new ArrayList<Card>(Arrays.asList(new Card("2", "♥", false, new Posn(8, 12)),
        new Card("4", "♠", false, new Posn(9, 13))));
    this.listtwo = new ArrayList<Card>(Arrays.asList(new Card("2", "♥", false, new Posn(8, 12)),
        new Card("4", "♠", false, new Posn(9, 13)), new Card("7", "♦", false, new Posn(3, 3)),
        new Card("9", "♠", false, new Posn(18, 25))));

    this.mt = new ArrayList<Card>();
    this.mixedList = new ArrayList<Card>(Arrays.asList(new Card("2", "♥", true, new Posn(8, 12)),
        new Card("4", "♠", false, new Posn(9, 13)), new Card("7", "♦", true, new Posn(3, 3)),
        new Card("9", "♠", false, new Posn(18, 25))));

    this.empty = new Deck(this.mt);
    this.mixedDeck = new Deck(this.mixedList);

    this.cwone = new ConcentrationWorld(new Random(3));
    this.cwtwo = new ConcentrationWorld(this.mixedDeck);
    this.cwthree = new ConcentrationWorld(this.empty);

  }

  // test makeScene 
  void testMakeScene(Tester t) {
    this.initData();

    WorldScene drawnOn = new WorldScene(SCREENWIDTH, SCREENHEIGHT + 100);
    drawnOn.placeImageXY(new TextImage(
        "Score: 0", 30, Color.GREEN), SCREENWIDTH / 2,  SCREENHEIGHT + 50);
    drawnOn.placeImageXY(new TextImage(
        "Time: 00:00", 30, Color.GREEN), SCREENWIDTH / 3, SCREENHEIGHT + 50);
    drawnOn.placeImageXY(this.ftwoH.drawCard(), 8, 12);
    drawnOn.placeImageXY(new Card("4", "♠", false, new Posn(9, 13)).drawCard(), 9, 13);
    drawnOn.placeImageXY(new Card("7", "♦", true, new Posn(3, 3)).drawCard(), 3, 3);
    drawnOn.placeImageXY(new Card("9", "♠", false, new Posn(18, 25)).drawCard(), 18, 25);


    WorldScene empTest1 = new WorldScene(SCREENWIDTH, SCREENHEIGHT + 100);
    empTest1.placeImageXY(new TextImage(""
        + "Score: 0", 30, Color.GREEN), SCREENWIDTH / 2,  SCREENHEIGHT + 50);
    empTest1.placeImageXY(new TextImage(
        "Time: 00:00", 30, Color.GREEN), SCREENWIDTH / 3, SCREENHEIGHT + 50);

    WorldScene empTest2 = new WorldScene(SCREENWIDTH, SCREENHEIGHT + 100);
    empTest2.placeImageXY(new TextImage(
        "Score: 0", 30, Color.GREEN), SCREENWIDTH / 2,  SCREENHEIGHT + 50);
    empTest2.placeImageXY(new TextImage(
        "Time: 00:00", 30, Color.GREEN), SCREENWIDTH / 3, SCREENHEIGHT + 50);

    t.checkExpect(this.cwone.makeScene(), this.cwone.deck.draw(empTest1));
    t.checkExpect(this.cwtwo.makeScene(), drawnOn) ;
    t.checkExpect(this.cwthree.makeScene(), empTest2);
  }

  // test onTick()
  void testOnTick(Tester t) {
    this.initData();

    Deck test = new Deck();
    test.fullDeck();
    ConcentrationWorld testcw = new ConcentrationWorld(new Random(3));

    t.checkExpect(this.cwone.deck, testcw.deck);
    this.cwone.onTick();
    t.checkExpect(this.cwone.deck, testcw.deck);

    t.checkExpect(this.cwtwo.deck, this.mixedDeck);
    this.cwtwo.onTick();
    t.checkExpect(this.cwtwo.deck, this.mixedDeck);

    t.checkExpect(this.cwthree.deck, this.empty);
    this.cwthree.onTick();
    // check for end of world
    /*
    t.checkExpect(this.cwthree.deck, this.);
    t.check
     */
  }

  // test lastScene
  void testLastScene(Tester t) {
    this.initData();

    ConcentrationWorld lastOne = new ConcentrationWorld();
    lastOne.deck = this.empty;

    WorldScene WSTest = new WorldScene(SCREENWIDTH, SCREENHEIGHT + 100);
    WSTest.placeImageXY(new TextImage("You Won!", 40, Color.BLACK),
        SCREENWIDTH / 2, SCREENHEIGHT / 2);
    WSTest.placeImageXY(new TextImage("Great Job Concentrating!", 30, Color.BLACK),
        SCREENWIDTH / 2, 3 * SCREENHEIGHT / 4);
    WSTest.placeImageXY(new TextImage("Total Score: 26", 30, Color.BLACK),
        2 * SCREENWIDTH / 3, SCREENHEIGHT); // total = 26, as no play has happened in this example
    WSTest.placeImageXY(new TextImage(
        "Total Time: 00:00", 30, Color.BLACK), SCREENWIDTH / 3, SCREENHEIGHT);

    // fix last scene test
    t.checkExpect(lastOne.lastScene("done"), WSTest);
  }

  // test onKeyEvent
  void testOnKeyEvent(Tester t) {
    this.initData();

    t.checkExpect(this.cwone, new ConcentrationWorld(new Random(3)));
    this.cwone.onKeyEvent("k");
    t.checkExpect(this.cwone, new ConcentrationWorld(new Random(3)));
    this.cwone.onKeyEvent("r");
    t.checkExpect(this.cwone == new ConcentrationWorld(new Random(3)), false);
    t.checkExpect(this.cwone.timer, 0);

    t.checkExpect(this.cwtwo, new ConcentrationWorld(this.mixedDeck));
    this.cwtwo.onKeyEvent("f");
    t.checkExpect(this.cwtwo, new ConcentrationWorld(this.mixedDeck));
    this.cwtwo.onKeyEvent("r");
    t.checkExpect(this.cwtwo == new ConcentrationWorld(this.mixedDeck), false);
    t.checkExpect(this.cwtwo.timer, 0);

    t.checkExpect(this.cwthree, new ConcentrationWorld(this.empty));
    this.cwthree.onKeyEvent("z");
    t.checkExpect(this.cwthree, new ConcentrationWorld(this.empty));
    this.cwthree.onKeyEvent("r");
    t.checkExpect(this.cwthree == new ConcentrationWorld(this.empty), false);
    t.checkExpect(this.cwthree.deck.size(), 52);
    t.checkExpect(this.cwthree.timer, 0);

  }

  // test onMouseClick
  void testOnMouseClicked(Tester t) {
    this.initData();

    ConcentrationWorld result1 = new ConcentrationWorld(new Random(3));
    result1.deck.findAndFlip(new Posn(50, 75));

    ArrayList<Card> mixedDeck2 = new ArrayList<Card>(Arrays.asList(
        new Card("2", "♥", true, new Posn(8, 12)),
        new Card("4", "♠", false, new Posn(9, 13)), 
        new Card("7", "♦", true, new Posn(3, 3)),
        new Card("9", "♠", false, new Posn(18, 25))));
    Deck result2 = new Deck(mixedDeck2);
    result2.findAndFlip(new Posn(2, 3));

    t.checkExpect(this.cwone, new ConcentrationWorld(new Random(3)));
    this.cwthree.onMouseClicked(new Posn(1600, 1700));
    t.checkExpect(this.cwone, new ConcentrationWorld(new Random(3)));
    this.cwone.onMouseClicked(new Posn(50, 75));
    t.checkExpect(this.cwone.deck, result1.deck);

    t.checkExpect(this.cwtwo.deck, this.mixedDeck);
    this.cwtwo.onMouseClicked(new Posn(50, 75));
    t.checkExpect(this.cwtwo.deck, this.mixedDeck);
    this.cwtwo.onMouseClicked(new Posn(2, 3));

    // this should be different
    t.checkExpect(this.cwtwo.deck, result2);

    t.checkExpect(this.cwthree.deck, this.empty);
    this.cwthree.onMouseClicked(new Posn(1600, 1700));
    t.checkExpect(this.cwthree.deck, this.empty);
    this.cwthree.onMouseClicked(new Posn(50, 50));
    t.checkExpect(this.cwthree.deck, this.empty);

  }

  // test shuffleDeck 
  void testShuffleDeck(Tester t) {
    this.initData();

    ConcentrationWorld cwtest =  new ConcentrationWorld(new Random(3));

    t.checkExpect(this.cwone.deck.size(), 52);
    t.checkExpect(this.cwone == cwone, true);
    t.checkExpect(this.cwone, cwtest);


    cwtest.shuffleDeck(new Random());

    t.checkExpect(this.cwone == cwtest, false);

    this.cwone.shuffleDeck(new Random());

    t.checkExpect(this.cwone == cwtest, false);

  }

  //test updateImgScore 
  void testUpdateImgScore(Tester t) {
    ConcentrationWorld w = new ConcentrationWorld();

    t.checkExpect(w.score, 26);
    w.updateImgScore();
    t.checkExpect(w.imgScore, new TextImage("Score: 26", 30, Color.GREEN));

    w.score = 14;
    w.updateImgScore(); 
    t.checkExpect(w.imgScore, new TextImage("Score: 14", 30, Color.GREEN));

    w.score = 0;
    w.updateImgScore();
    t.checkExpect(w.imgScore, new TextImage("Score: 0", 30, Color.GREEN));
  }

  //test time
  void testTime(Tester t) {
    this.initData();

    ConcentrationWorld c1 = new ConcentrationWorld();

    t.checkExpect(c1.time(), "00:00");

    c1.onTick();

    t.checkExpect(c1.time(), "00:01");

    for (int i = 0; i < 59; i++) {
      c1.onTick();
    }
    t.checkExpect(c1.time(), "01:00");

    c1.onTick();
    t.checkExpect(c1.time(), "01:01");
  }

  // Deck tests
  // test isEmpty 
  void testIsEmpty(Tester t) {
    this.initData();

    t.checkExpect(this.firstrow.isEmpty(), false);
    t.checkExpect(this.mt.isEmpty(), true);
    t.checkExpect(this.listtwo.isEmpty(), false);
  }

  // test get 
  void testGet(Tester t) {
    this.initData(); 

    t.checkExpect(this.firstrow.get(0), new Card("2", "♥", false, new Posn(8, 12)));
    t.checkExpect(this.firstrow.get(1), new Card("4", "♠", false, new Posn(9, 13)));
    t.checkExpect(this.listtwo.get(2), new Card("7", "♦", false, new Posn(3, 3)));
  }

  // test size 
  void testSize(Tester t) {
    this.initData();

    t.checkExpect(this.firstrow.size(), 2);
    t.checkExpect(this.mt.size(), 0);
    t.checkExpect(this.listtwo.size(), 4);
  }

  // test draw

  void testDrawD(Tester t) {

    this.initData();

    WorldScene ws = new WorldScene(SCREENWIDTH, SCREENHEIGHT);

    this.empty.draw(ws);

    WorldScene mws = new WorldScene(SCREENWIDTH, SCREENHEIGHT);
    mws.placeImageXY(new Card("2", "♥", true, new Posn(8, 12)).drawCard(), 8, 12);
    mws.placeImageXY(new Card("4", "♠", false, new Posn(9, 13)).drawCard(), 9, 13);
    mws.placeImageXY(new Card("7", "♦", true, new Posn(3, 3)).drawCard(), 3, 3);
    mws.placeImageXY(new Card("9", "♠", false, new Posn(18, 25)).drawCard(), 18, 25);


    t.checkExpect(this.empty.draw(ws), ws);

    t.checkExpect(this.mixedDeck.draw(ws), mws);



  }

  // test add 
  void testAdd(Tester t) {
    this.initData();

    this.mt.add(new Card("A", "♥", false, new Posn(10, 35)));
    this.firstrow.add(new Card("A", "♥", false, new Posn(10, 35)));
    this.listtwo.add(new Card("A", "♥", false, new Posn(10, 35)));

    t.checkExpect(this.mt, 
        new ArrayList<Card>(Arrays.asList(new Card("A", "♥", false, new Posn(10, 35)))));
    t.checkExpect(this.firstrow,
        new ArrayList<Card>(Arrays.asList(new Card("2", "♥", false, new Posn(8, 12)),
            new Card("4", "♠", false, new Posn(9, 13)),
            new Card("A", "♥", false, new Posn(10, 35)))));
    t.checkExpect(this.listtwo, 
        new ArrayList<Card>(Arrays.asList(new Card("2", "♥", false, new Posn(8, 12)),
            new Card("4", "♠", false, new Posn(9, 13)), 
            new Card("7", "♦", false, new Posn(3, 3)),
            new Card("9", "♠", false, new Posn(18, 25)),
            new Card("A", "♥", false, new Posn(10, 35)))));
  }

  // test remove write more tests 
  void testRemove(Tester t) {
    this.initData();

    this.firstrow.remove(0);
    this.listtwo.remove(0);

    t.checkExpect(this.firstrow,
        new ArrayList<Card>(Arrays.asList(new Card("4", "♠", false, new Posn(9, 13)))));
    t.checkExpect(this.listtwo,
        new ArrayList<Card>(Arrays.asList(new Card("4", "♠", false, new Posn(9, 13)),
            new Card("7", "♦", false, new Posn(3, 3)),
            new Card("9", "♠", false, new Posn(18, 25))))); 
  }

  //test find and flip
  void testFindAndFlip(Tester t) {
    this.initData();

    t.checkExpect(empty.flippedList, new ArrayList<>());
    empty.findAndFlip(new Posn(0, 0));
    t.checkExpect(empty.flippedList, new ArrayList<>());

    t.checkExpect(mixedDeck.flippedList, new ArrayList<>());

    mixedDeck.findAndFlip(new Posn(8, 12)); 
    t.checkExpect(mixedDeck.flippedList, 
        new ArrayList<>(Arrays.asList(new Card("4", "♠", true, new Posn(9, 13)), 
            new Card("9", "♠", true, new Posn(18, 25) )))); // fails

    mixedDeck.findAndFlip(new Posn(9, 13)); 
    t.checkExpect(mixedDeck.flippedList, 
        new ArrayList<>(Arrays.asList(
            new Card("4", "♠", true, new Posn(9, 13)), 
            new Card("9", "♠", true, new Posn(18, 25))))); 

    mixedDeck.findAndFlip(new Posn(0, 0)); 
    t.checkExpect(mixedDeck.flippedList, 
        new ArrayList<>(Arrays.asList(
            new Card("4", "♠", true, new Posn(9, 13)), 
            new Card("9", "♠", true, new Posn(18, 25)))));  

    mixedDeck.findAndFlip(new Posn(300, 300)); 
    t.checkExpect(mixedDeck.flippedList, 
        new ArrayList<>(Arrays.asList(
            new Card("4", "♠", true, new Posn(9, 13)), 
            new Card("9", "♠", true, new Posn(18, 25)))));  

    mixedDeck.findAndFlip(new Posn(300, 300)); 
    t.checkExpect(mixedDeck.flippedList, 
        new ArrayList<>(Arrays.asList(
            new Card("4", "♠", true, new Posn(9, 13)), 
            new Card("9", "♠", true, new Posn(18, 25)))));  

  }


  // test removeMatch
  void testRemoveMatch(Tester t) {
    this.initData();

    t.checkExpect(this.empty, new Deck(new ArrayList<Card>()));
    this.empty.removeMatch();
    t.checkExpect(this.empty, new Deck(new ArrayList<Card>()));

    t.checkExpect(this.cwone.deck.size(), 52);
    this.cwone.deck.removeMatch();
    t.checkExpect(this.cwone.deck.size(), 52);

    t.checkExpect(this.mixedDeck.size(), 4);
    // t.checkExpect(this.mixedDeck, null);
    this.mixedDeck.removeMatch();
    t.checkExpect(this.mixedDeck.size(), 4);

    Deck match = new Deck(new ArrayList<Card>(Arrays.asList(
        new Card("4", "♠", true, new Posn(9, 13)), new Card("4", "♣", true, new Posn(9, 13)))));
    this.flippedList.add(new Card("4", "♣", true, new Posn(9, 13)));
    this.flippedList.add(new Card("4", "♠", true, new Posn(9, 13)));

    t.checkExpect(match.size(), 2);
    match.removeMatch();
    t.checkExpect(match.size(), 0);
    t.checkExpect(match, this.empty);

  }


  //test fullDeck 
  void testFullDeck(Tester t) {
    this.initData(); 

    this.empty.fullDeck();

    // checks that deck contains 52 cards 
    t.checkExpect(this.empty.size(), 52);

    // check that size of empty != 
    t.checkExpect(this.empty.size() == new Deck(new ArrayList<Card>()).size(), false);

  }



  // Card tests

  // test adjust
  void testAdjust(Tester t) {
    this.initData();

    t.checkExpect(this.oneH.posn, new Posn(0,0));

    this.oneH.adjust(2, 3);

    t.checkExpect(this.oneH.posn.x, 2);
    t.checkExpect(this.oneH.posn.y, 3);
    t.checkExpect(this.twoH.posn, new Posn(0,0));

    this.twoH.adjust(4, 5);

    t.checkExpect(this.twoH.posn.x, 4);
    t.checkExpect(this.twoH.posn.y, 5);

    this.twoH.adjust(4, 5);

    t.checkExpect(this.twoH.posn.x, 4);
    t.checkExpect(this.twoH.posn.y, 5);

    this.twoH.adjust(0,  0);

    t.checkExpect(this.twoH.posn.x, 0);
    t.checkExpect(this.twoH.posn.y, 0);
  }

  // test drawCard()

  void testDrawCard(Tester t) {

    this.initData();

    t.checkExpect(this.oneH.drawCard(), new OverlayImage(
        new FromFileImage("src/BOCShrek.png"),
        new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE)));

    t.checkExpect(this.twoH.drawCard(), new OverlayImage(
        new FromFileImage("src/BOCShrek.png"),
        new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE)));

    t.checkExpect(this.ftwoH.drawCard(), new OverlayImage(
        new FromFileImage("src/2♥.png"),
        new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE)));

    t.checkExpect(this.foneH.drawCard(), new OverlayImage(
        new FromFileImage("src/BOCShrek.png"),
        new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE)));

    this.foneH.flip();

    t.checkExpect(this.foneH.drawCard(), new OverlayImage(
        new FromFileImage("src/A♥.png"),
        new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE)));

    t.checkExpect(new  Card("9", "♠", true, new Posn(18, 25)).drawCard(), new OverlayImage(
        new FromFileImage("src/9♠.png"),
        new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE)));

    t.checkExpect(new  Card("9", "♦", true, new Posn(18, 25)).drawCard(), new OverlayImage(
        new FromFileImage("src/9♦.png"),
        new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE)));

    t.checkExpect(new  Card("9", "♣", true, new Posn(18, 25)).drawCard(), new OverlayImage(
        new FromFileImage("src/9♣.png"),
        new RectangleImage(GRIDWIDTH, GRIDHEIGHT, OutlineMode.SOLID, Color.WHITE)));
  }





  // test draw
  void testDrawC(Tester t) {
    this.initData();

    WorldScene wstest = new WorldScene(SCREENWIDTH, SCREENHEIGHT);

    //t.checkExpect(WS, wstest);

    this.oneH.draw(WS);

    wstest.placeImageXY(this.oneH.drawCard(), 0, 0);

    // t.checkExpect(WS, wstest);

    this.twoH.draw(WS);

    wstest.placeImageXY(this.twoH.drawCard(), 0, 0);

    // t.checkExpect(WS, wstest);
  }


  // test flip
  void testFlip(Tester t) {
    this.initData();

    this.oneH.flip();

    t.checkExpect(this.oneH.flipped, true);

    this.twoH.flip();

    t.checkExpect(this.oneH.flipped, true);

    this.ftwoH.flip();

    t.checkExpect(this.ftwoH.flipped, false);
  }


  // test find 
  void testFind(Tester t) {
    this.initData();

    t.checkExpect(this.oneH.find(new Posn(0, 0)), true);
    t.checkExpect(this.oneH.find(new Posn(10, 0)), true);
    t.checkExpect(this.oneH.find(new Posn(20, 0)), true);
    t.checkExpect(this.oneH.find(new Posn(30, 0)), true);
    t.checkExpect(this.oneH.find(new Posn(101, 0)), false);
    t.checkExpect(this.oneH.find(new Posn(0, 30)), true);
    t.checkExpect(this.oneH.find(new Posn(0, 50)), true);
    t.checkExpect(this.oneH.find(new Posn(0, 101)), false);

    t.checkExpect(this.twoH.find(new Posn(0, 0)), true);
    t.checkExpect(this.threeH.find(new Posn(0, 0)), true);
    t.checkExpect(this.fourH.find(new Posn(0, 0)), true);
    t.checkExpect(this.fiveH.find(new Posn(0, 0)), true);
    t.checkExpect(this.sixH.find(new Posn(0, 0)), true);
    t.checkExpect(this.foneH.find(new Posn(0, 0)), true);

    t.checkExpect(this.oneH.find(new Posn(200, 0)), false);
    t.checkExpect(this.oneH.find(new Posn(0, 200)), false);
    t.checkExpect(this.oneH.find(new Posn(200, 200)), false);
  }


  // test cardsSame 
  void testCardsSame(Tester t) {
    this.initData();

    t.checkExpect(this.foneH.cardsSame(new Card("A", "♠", false, new Posn(15, 7))), false);
    t.checkExpect(this.foneH.cardsSame(new Card("A", "♦", false, new Posn(15, 7))), true);
    t.checkExpect(this.foneH.cardsSame(new Card("5", "♦", false, new Posn(15, 7))), false);
    t.checkExpect(this.foneH.cardsSame(new Card("5", "♠", false, new Posn(15, 7))), false);
    t.checkExpect(this.ftwoH.cardsSame(new Card("2", "♥", false, new Posn(9, 31))), true);
    t.checkExpect(this.ftwoH.cardsSame(new Card("2", "♦", false, new Posn(9, 31))), true);
    t.checkExpect(this.foneH.cardsSame(new Card("5", "♠", false, new Posn(15, 7))), false);
  }


  // Big bang
  void testBigBang(Tester t) {

    ConcentrationWorld world1 = new ConcentrationWorld(new Random(50));
    int worldWidth = SCREENWIDTH;
    int worldHeight = SCREENHEIGHT + 100;
    double tickRate = 1;
    world1.bigBang(worldWidth, worldHeight, tickRate);
  }

}
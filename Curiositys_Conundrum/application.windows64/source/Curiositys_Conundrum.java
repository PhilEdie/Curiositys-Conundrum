import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Curiositys_Conundrum extends PApplet {

HashMap<String, PImage> allImages = new HashMap<String, PImage>();
Main main;
int keyAmount = 128;

public void setup() {
  this.importImages();
  main = new Main();
  
  stroke(255);
}

public void draw() {
  main.update();
}

public void keyPressed() {
  //Prevents a crash when pressing shift + any key
  if (keyCode > keyAmount) {
    keyCode = '0';
  }
  main.keys[keyCode] = true;
  
  //Pressing enter restarts the game. 
  if(key == ENTER){ main = new Main(); }
}


public void keyReleased() {
  //Prevents a crash when pressing shift + any key
  if (keyCode > keyAmount) {
    keyCode = '0';
  }

  main.keys[keyCode] = false;
}

public void mousePressed() {
  main.getRover().firing = true;
}

public void mouseReleased() {
  main.getRover().firing = false;
}


public void importImages(){
  this.allImages.put("Mothership", loadImage("MothershipBody.png"));
  this.allImages.put("Rover", loadImage("RoverBody.png"));
  this.allImages.put("BombUFO", loadImage("UFOBody.png"));
  this.allImages.put("LaserUFO", loadImage("LaserUFOBody.png"));
  this.allImages.put("GunnerUFO", loadImage("GunnerUFOBody.png"));
}
class Background {
  float animSpeed = 1;
  float waitedFrames = 0; 
  float baseY;
  int[] colorValues;
  float xoff = 0.0f;
  float yoff = 0.0f;
  float xOffIncrement = 0.01f;
  float yOffIncrement = 0.1f;
  ArrayList<Float> xPoints = new ArrayList<Float>();
  ArrayList<Float> yPoints = new ArrayList<Float>();

  //Initialises the background with random perlin noise hills, ranging from -50 to + 50 off the baseY.
  Background(int[] colorValues, float baseY, float animSpeed, float startxOff) {
    this.xoff = startxOff;
    this.colorValues = colorValues;
    this.baseY = baseY;
    this.animSpeed = animSpeed;

    for (float x = 0; x <= width; x += 10) {
      this.xPoints.add(x);
      float y = map(noise(xoff, yoff), 0, 1, this.baseY-50, this.baseY+50);
      this.yPoints.add(y);
      this.xoff += xOffIncrement;
      this.yoff += yOffIncrement;
    }
  }
  
  //Removes the first x and y coordinate each time update is called. This provides the illusion of moving right. Redraws the hills depending on the waited frames. 
  public void update() {
    if (waitedFrames >= 1) {
      yPoints.remove(0);
      yPoints.add(map(noise(xoff, yoff), 0, 1, this.baseY-50, this.baseY+50));
      xoff += xOffIncrement;
      yoff += yOffIncrement;
      
      //The noise will loop once xoff and yoff is greater than 100. 
      if (xoff > 100) { xoff = 0; }
      if (yoff > 100) { yoff = 0; }
      
      this.waitedFrames = 0;
    }
    
      fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
      noStroke();
      beginShape();
      for (int i = 0; i < this.xPoints.size(); i++) {
        vertex(this.xPoints.get(i), this.yPoints.get(i));
      }
      vertex(width, height);
      vertex(0, height);
      endShape(CLOSE);
      this.waitedFrames += this.animSpeed;
    }
  }
class Bomb {
  PVector position = new PVector();
  float size = 60;
  float maxSize = 200;
  PVector speed = new PVector();
  float damage;
  boolean exploding = false;
  boolean hitMaxSize = false;
  float scoreValue = 50;
  boolean alreadyExploded = false;



  Bomb(float x, float y, float size, float speed) {
    this.size = size;
    this.position.x = x;
    this.position.y = y;
    this.speed.y = speed;
  }

  public void update() {
    this.moveBomb();
    this.checkBulletCollisions();
    if (this.exploding) {
      this.checkBombCollisions();
      this.growExplosion();
    }
    this.display();
  }


  public void display() {
    noStroke();
    fill(255);
    ellipse(this.position.x, this.position.y, this.size, this.size);
  }


//Increases the y when not exploding. Starts explosion when hitting the ground. 
  public void moveBomb() {
    if (!this.exploding) {
      this.position.y += this.speed.y;
    }
    if (this.hitTheGround()) {
      this.startExplosion();
    }
  }

//Sets exploding and alreadyExploded to true. Plays an explosion sound. 
  public void startExplosion() {
    this.exploding = true;
    if (!this.alreadyExploded) { 
     // allSounds.get("Bomb").play();
      this.alreadyExploded = true;
    }
  }

//Increases the radius of the bomb. Deletes bomb when it gets too big. 
  public void growExplosion() {
    this.size +=20;
    if (this.size > this.maxSize) {
      main.removeBomb(this);
    }
  }



//Starts the explosion if hit by a bullet. Increases score. 
  public void checkBulletCollisions() {
    for (Bullet b : main.activeFriendlyBullets) {
      if (!this.alreadyExploded && this.collides(b.getX(), b.getY(), b.getSize(), b.getSize())) {
        main.score.increaseScore(this.scoreValue);
        this.startExplosion();
      }
    }
  }
  
  
  //Called while exploding. Makes other bombs explode if they collide with the blast radius. Increases score. 
  public void checkBombCollisions() {
    for (Bomb b : main.activeBombs) {
      if (!b.alreadyExploded && !this.equals(b) && this.collides(b.getX(), b.getY(), b.getSize(), b.getSize())) {
        main.score.increaseScore(this.scoreValue);
        b.startExplosion();
      }
    }
  }


  public float getX() {
    return this.position.x;
  }

  public float getY() {
    return this.position.y;
  }

  public float getSize() {
    return this.size;
  }

  public boolean hitTheGround() {
    return (this.position.y > height/2 + height/3);
  }

  public boolean collides(float otherX, float otherY, float otherW, float otherH) {
    if (this.position.x + this.size > otherX
      && this.position.x < otherX + otherW
      && this.position.y + this.size > otherY
      && this.position.y < otherY + otherH) {
      return true;
    }  
    return false;
  }
}
class BombUFO extends UFO {

  float bombSize = 30;
  float bombSpeed = 10;
  float scoreValue = 100;

  BombUFO(int[] colorValues, float x, float y) {
    super(colorValues, x, y);
    this.UFOBody = allImages.get("BombUFO");
  }

  public void update() {
    this.checkCollisions();
    this.display();
    this.checkOutOfBounds();
    this.dropBomb();
    position.x += speed;
  }


//Drops a bomb when above the rover. 
  public void dropBomb() {
    if (this.ammo > 0) {
      if (this.position.x > main.rover.getX()
        && this.position.x < main.rover.getX() + main.rover.getWidth()) {
        main.activeBombs.add(new Bomb(this.position.x, this.position.y, this.bombSize, this.bombSpeed));
        this.ammo--;
      }
    }
  }
}
class Bullet {
  int[] colorValues;
  PVector position = new PVector(0, 0);
  float size = 15;
  PVector speed = new PVector(0, 0);
  float damage;

  Bullet(int[] colorValues, PVector position, PVector speed) {
    this.colorValues = colorValues;
    this.position = position;
    this.speed = speed;
  }

  public void update() {
    this.checkOutOfBounds();
    this.moveBullet();
    this.display();
  }

  public void display() {
    fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    noStroke();
    ellipse(this.position.x, this.position.y, this.size, this.size);
  }

  public void moveBullet() { 
    this.position.add(this.speed);
  } 


//Returns true if bullet is off screen. 
  public boolean outOfBounds() {
    return(this.position.x > width || this.position.x < 0 
      || this.position.y > height || this.position.y < 0);
  }


//Removes the bullet if it leaves the screen. 
  public void checkOutOfBounds() {
    if (this.outOfBounds()) { 
      main.deadFriendlyBullets.add(this);
      main.deadEnemyBullets.add(this);
    }
  }
  
   public boolean collides(float otherX, float otherY, float otherW, float otherH) {
    if (this.position.x + this.size > otherX
      && this.position.x < otherX + otherW
      && this.position.y + this.size > otherY
      && this.position.y < otherY + otherH) {
      return true;
    }  
    return false;
  }
  
  
  public float getX() { 
    return this.position.x;
  }

  public float getY() { 
    return this.position.y;
  }

  public float getSize() { 
    return this.size;
  }
}
class ExplodingMothership {
  PVector position = new PVector(0, 0);
  float size = 5;
  float increaseSize = 10;
  float maxSize = 200;

  
  //Animations similar to the Bomb class, except only used when the Mothership is dying. 
  ExplodingMothership(float x, float y){
    this.position.x = x;
    this.position.y = y;
  }
  
  
  public void update(){
    this.size += this.increaseSize;
    if(this.size >= this.maxSize){
      main.getMothership().removeExplosion(this);
    }
    fill(255);
    noStroke();
    ellipse(this.position.x, this.position.y, this.size, this.size);
  }
}
class Explosion {
  ArrayList<Particle> particles;
  ArrayList<Particle> deadParticles = new ArrayList<Particle>();
  PVector origin;
  int[] colorValues;
  int numParticles = 10;

  //Controls an array of particles which move independently. Called when destroying a basic UFO or shooting the Mothership.
  Explosion(int[] colorValues, PVector position, int numParticles) {
    this.colorValues = colorValues;
    this.origin = position.copy();
    this.numParticles = numParticles;
    this.particles = new ArrayList<Particle>();
    for (int i = 0; i < this.numParticles; i++) { 
      this.addParticle();
    }
  }

  public void addParticle() { 
    this.particles.add(new Particle(this.colorValues, this.origin));
  }

  //Removes the explosion object if all particles are dead. Otherwise, updates the remaining particles. 
  public void updateParticles() {
    if (completedExplosion()) {
      main.deadExplosions.add(this);
      return;
    }
    for (Particle p : this.particles) {     
      p.update();    
      p.display();
      if (p.isDead()) {
        this.deadParticles.add(p);
      }
    }
    this.particles.removeAll(this.deadParticles);
  }


  //Checks to see if any particles are still active. A particle is no longer active when it is off screen. 
  public boolean completedExplosion() {
    for (Particle p : this.particles) {
      if (!p.isDead()) { 
        return false;
      }
    }
    return true;
  }
}
class GUI {
  HealthBar healthBar;
  float textDelay = 2000;
  float displayStart = millis();
  String lastUpgrade;
  int upgradeOpacity = 0;
  int waveNumOpacity = 0;
  float controlsOpacity = 255;
  int lastWaveNum = 1;
  boolean gameOverSoundPlayed = false;

  GUI(int livesLeft, int[] colorValues) {
    this.healthBar = new HealthBar(livesLeft, colorValues);
  }

  public void update() {
    if (main.active) {
      this.healthBar.update();
    }
    this.displayControls();
    this.displayUpgrade();
    this.displayWaveNum();
    this.displayLeftInWave();
    if (main.lostGame) {
      this.drawLostGame();
    }
  }

//Shows the controls at the start of the game. 
  public void displayControls() {
    if (this.controlsOpacity > 0) {
      fill(255, 255, 255, this.controlsOpacity);
      this.controlsOpacity = this.controlsOpacity - 0.5f;
      textSize(100);
      textAlign(CENTER, CENTER);
      text("A and D to move, Mouse to shoot.", width/2, height/2 + 100);
    }
  }

  //Draws the most recent upgrade on screen. eg: "Fire Rate Increased"
  public void displayUpgrade() {
    if (this.upgradeOpacity > 0) {
      fill(255, 255, 255, this.upgradeOpacity);
      this.upgradeOpacity--;
      textSize(100);
      textAlign(CENTER, CENTER);
      text(this.lastUpgrade + " Increased", width/2, height/2);
    }
  }



  //Shows how many UFOs are left in the wave up the top right of the screen. 
  public void displayLeftInWave() {
    fill(255);
    textSize(50);
    textAlign(RIGHT, TOP);
    text("UFO's Remaining: " + (main.getUFOsLeft()), width -20, 10);
  }


  //Sets the upgrade to be displayed by displayUpgrade()
  public void setUpgrade(String name) {
    this.lastUpgrade = name;
    this.upgradeOpacity = 255;
  }


  //Displays the current wave on screen. Called when the player moves to the next wave
  public void displayWaveNum() {
    fill(255, 255, 255, this.waveNumOpacity);
    this.waveNumOpacity--;
    textSize(100);
    textAlign(CENTER, CENTER);
    text("Wave " + (this.lastWaveNum), width/2, height/2);
  }

  public void setWaveNum(int wave) {
    this.lastWaveNum = wave;
    this.waveNumOpacity = 255;
  }

  //Game over screen. Shows the last wave, the score, and how to play again
  public void drawLostGame() {
    fill(255);
    textSize(100);
    textAlign(CENTER, CENTER);
    text("GAME OVER", width/2, height/2 - 100);
    text("Wave: " + (main.getWave()), width/2, height/2);
    text("Score: " + round(main.score.getCurrentScore()), width/2, height/2 + 100);
    text("Play Again? Press Enter", width/2, height/2 + 200);
    //if(!this.gameOverSoundPlayed){ allSounds.get("Death").play();}
    this.gameOverSoundPlayed = true;
  }
}
class GunnerUFO extends UFO {
  int[] colorValues;
  float bulletSpeed = 10;
  float bulletAngle = PI/15;
  float gunLength = 50;


  GunnerUFO(int[] colorValues, float x, float y) {
    super(colorValues, x, y);
    this.colorValues = new int[] {0, 255, 255};
    this.UFOBody = allImages.get("GunnerUFO");
    this.scoreValue = 300;
  }

  public void update() {
    this.checkCollisions();
    this.checkOutOfBounds();
    this.display();
    if (this.ammo > 0) {
      this.shoot();
    }
    position.x += speed;
  }

  public void display() {
    tint(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    noStroke();
    image(this.UFOBody, this.position.x, this.position.y);
  }


//Shoots 3 bullets in a cone. Adds each bullet to an arraylist so that each bullet leaves the UFO at the same time. 
  public void shoot() {
    
    if (this.position.x > main.rover.getX()
      && this.position.x < main.rover.getX() + main.rover.getWidth()) {

      ArrayList<Bullet> wave = new ArrayList<Bullet>();   
      PVector bulletPos = new PVector((this.position.x + this.w) - this.w/2, this.position.y + gunLength); 
      PVector speed = new PVector(0, this.bulletSpeed);

      //Bullet 1
      wave.add(new Bullet(this.colorValues, bulletPos, speed));        

      //Bullet 2
      bulletPos = new PVector((this.position.x + this.w)- this.w/2 + 10, this.position.y + gunLength); 
      speed = new PVector(sin(bulletAngle) * this.bulletSpeed, cos(bulletAngle) * this.bulletSpeed);
      wave.add(new Bullet(this.colorValues, bulletPos, speed)); 

      //Bullet 3
      bulletPos = new PVector((this.position.x + this.w)- this.w/2 - 10, this.position.y + gunLength); 
      speed = new PVector(sin(- bulletAngle) * this.bulletSpeed, cos(- bulletAngle) * this.bulletSpeed);
      wave.add(new Bullet(this.colorValues, bulletPos, speed));  

      main.activeEnemyBullets.addAll(wave);
      this.ammo--;
    }
  }
}
class HealthBar {
  int[] colorValues;
  int maxLives;
  int livesLeft;
  PVector position;
  float w = 100;
  float h = 20;
  float lastLostLife = millis();
  float invincibleTime = 1000;

  HealthBar(int lives, int[] colorValues) {
    this.colorValues = colorValues;
    this.maxLives = lives;
    this.livesLeft = lives;
    this.position = new PVector(10, 10);
  }

  public void update() {
    fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    textSize(50);
    textMode(CORNER);
    fill(255);
    noStroke();
    text("Lives: ", this.position.x, this.position.y + 50);
    fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    for(int i = 0; i < this.livesLeft; i++){
       ellipse(this.position.x + 180 + (50 * i), this.position.y + 35, 40, 40); 
    }
    
    if (this.livesLeft <= 0) {
      main.loseGame();
    }
  }

//Decreases the lives left. The player has 1000 ms of invincibility after losing a life. 
  public void loseLife() {
    if (millis() - lastLostLife > this.invincibleTime
      && this.livesLeft > 0) {
      this.livesLeft--;
      this.lastLostLife = millis();
    }
  }
}
class Laser {
  PVector position;
  float w;
  float lifeTime;


  Laser(float x, float y, float w, float lifeTime) {
    this.position = new PVector(x, y);
    this.w = w;
    this.lifeTime = lifeTime;
  }
  
  //Keeps the laser moving on the x axis at the same rate as its UFO. draws a growing laser beam from the UFO to the ground. 
  public void update(float speed){
    this.position.x += speed;
    this.w += 1;
    fill(255);
    rectMode(CORNER);
    rect(this.position.x - w/2, this.position.y + 6, w, height);
    this.lifeTime--;
  }
  
  public float getX(){
    return this.position.x;
  }
  
  public float getY(){
   return this.position.y; 
  }
  
  public float getWidth(){
   return this.w; 
  }
  
  public float getHeight(){
    return height;    
  }
  
  public float getLifeTime(){
    return this.lifeTime;
  }

  public boolean isComplete(){
    return this.lifeTime <= 0;
  }
}
class LaserUFO extends UFO {

  float aimRange = 500;
  boolean charging = false;
  float currentChargeTime = 0;
  float maxChargeTime = 50;

  boolean firing = false;
  float currentFiringTime = 0;
  float maxFiringTime = 20;

  float beamX;
  float beamY;
  float beamWidth = 5;
  Laser activeLaser;

  LaserUFO(int[] colorValues, float x, float y) {
    super(colorValues, x, y);
    this.UFOBody = allImages.get("LaserUFO"); 
    this.beamX = this.position.x + 100;
    this.beamY = this.position.y + this.h;
    this.scoreValue = 500;
    this.ammo = 1;
  }

  public void update() {
    this.checkOutOfBounds();
    this.checkCollisions();
    this.display();
    this.updateLaser();
    if (this.ammo > 0) { 
      this.attemptToShoot();
    }
    if (this.charging) { 
      this.chargeLaser();
    }
    position.x += speed;
    this.beamX = this.position.x + 100;
    this.beamY = this.position.y + this.h;
  }

  public void display() {
    tint(255, 0, 255);
    fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    noStroke();
    image(this.UFOBody, this.position.x, this.position.y);
  }

//Updates the laser. Removes the laser if the laser has completed its animation. 
  public void updateLaser() {
    if (this.activeLaser == null) { 
      return;
    }
    this.activeLaser.update(this.speed);
    if (this.activeLaser.isComplete()) {
      main.removeLaser(this.activeLaser);
      this.activeLaser = null;
    }
  }

//Begins to charge laser when in range of Rover. 
  public void attemptToShoot() {
    if (this.aboveRover()) {
      this.currentChargeTime = 0;
      this.charging = true;  
      this.ammo--;
    }
  }

//Displays red aiming beam, waits until it reaches maxChargeTime before firing laser. 
  public void chargeLaser() {
    fill(255, 0, 0);
    rectMode(CORNER);
    rect(this.beamX - 1, this.beamY + 6, 2, height);
    this.currentChargeTime++;
    if (this.currentChargeTime >= this.maxChargeTime) {
      this.currentFiringTime = 0;
      this.activeLaser = new Laser(this.beamX, this.beamY, this.beamWidth, this.maxFiringTime);
      main.activeLasers.add(this.activeLaser);
      this.charging = false;
    }
  }


//Checks if it is near the rover. 
  public boolean aboveRover() {
    return(this.position.x > main.rover.getX() - this.aimRange
      && this.position.x < main.rover.getX() + main.rover.getWidth() + this.aimRange);
  }

//Removes the UFO and its laser when destroyed.
  public void loseHealth() {
    if (this.hasHealthLeft()) {
      this.health--;
    } else {
      main.removeLaser(this.activeLaser);
      this.activeLaser = null;
      this.destroyUFO();
    }
  }
}
class Main {

  //https://www.youtube.com/watch?v=nermdWNIhm0 help with key use
  boolean[] keys = new boolean[128];

  GUI GUI;
  Rover rover;
  Score score;

  //Variables to hold color palette. 

  int color0[] = {30, 20, 70}; 
  int color1[] = {106, 44, 112}; 
  int color2[] = {184, 59, 94}; 
  int color3[] = {220, 118, 73};  
  int color4[] = {238, 236, 218};



  //Lists of all active Objects

  ArrayList<Background> backgroundLayers = new ArrayList<Background>();
  ArrayList<Upgrade> activeUpgrades = new ArrayList<Upgrade>();
  ArrayList<Explosion> activeExplosions = new ArrayList<Explosion>();
  ArrayList<Bullet> activeFriendlyBullets = new ArrayList<Bullet>();
  ArrayList<Bullet> activeEnemyBullets = new ArrayList<Bullet>();
  ArrayList<UFO> activeUFOs = new ArrayList<UFO>(); 
  ArrayList<Bomb> activeBombs = new ArrayList<Bomb>();
  ArrayList<Laser> activeLasers = new ArrayList<Laser>();


  //Temporary lists for dead objects. activeObjects use .removeAll() on deadObjects. 
  Mothership mothership;
  ArrayList<Laser> deadLasers = new ArrayList<Laser>();
  ArrayList<Bomb> deadBombs = new ArrayList<Bomb>();
  ArrayList<UFO> deadUFOs = new ArrayList<UFO>();
  ArrayList<Bullet> deadEnemyBullets = new ArrayList<Bullet>();
  ArrayList<Bullet> deadFriendlyBullets = new ArrayList<Bullet>();
  ArrayList<Explosion> deadExplosions = new ArrayList<Explosion>();
  ArrayList<Upgrade> deadUpgrades = new ArrayList<Upgrade>();

  //Keeps track of waves and total ufos. 
  int waveNum = 1;
  int UFOsInWave = 5;
  int UFOsLeft = UFOsInWave;


  //Probabilities for upgrades and UFOs
  float probUpgrade = 10;
  float probUFO = 1;
  float increaseProbUFO = 0.02f;


  //Constant for the height of the ground
  float groundY = height/2 + height/3;


  boolean active = true;
  boolean lostGame = false;
  int livesLeft = 3;
  float bossWave = 5;  //Every 5 waves a boss will spawn. 


//The main class keeps track of all of the games objects. creating a new main object restarts the game. 

  Main() {
    this.GUI = new GUI(this.livesLeft, this.color4);
    GUI.setWaveNum(this.waveNum);
    rover = new Rover(this.color4, width/2, this.groundY + 20);
    backgroundLayers.add(new Background(this.color1, height/4, 0.5f, 0));
    backgroundLayers.add(new Background(this.color2, height/3, 1, 0));
    backgroundLayers.add(new Background(this.color3, this.groundY, 1, 50));
    this.score = new Score();
  }


  public void update() {
    clear();
    background(this.color0[0], this.color0[1], this.color0[2]);
    if (this.UFOsLeft <= 0) { 
      this.startNextWave();
    }
    this.removeDeadObjects();
    this.updateAllObjects();
    this.checkPressedKeys();
    this.GUI.update();
    if (this.waveNum % this.bossWave == 0) {
      this.startBossFight();
      return;
    }
    if (this.activeUFOs.size() < this.UFOsLeft) { 
      this.spawnUFO();
    }
  }


  //The player can use A OR Left arrow to move reft.
  //The player can use D OR Right arrow to move right. 
  public void checkPressedKeys() {
    if (keys['a'] || keys['A'] || keys[LEFT]) { 
      rover.move(-1);
    }
    if (keys['d'] || keys['D'] || keys[RIGHT]) { 
      rover.move(1);
    }
  }

  //Clears all dead objects from the activeObjects lists.
  public void removeDeadObjects() {
    this.activeUpgrades.removeAll(this.deadUpgrades);
    this.activeFriendlyBullets.removeAll(this.deadFriendlyBullets);
    this.activeEnemyBullets.removeAll(this.deadEnemyBullets);
    this.activeUFOs.removeAll(this.deadUFOs);
    this.activeBombs.removeAll(this.deadBombs);
    this.activeExplosions.removeAll(this.deadExplosions);
    this.activeLasers.removeAll(this.deadLasers);

    this.deadUpgrades.clear();
    this.deadFriendlyBullets.clear();
    this.deadEnemyBullets.clear();
    this.deadUFOs.clear();
    this.deadBombs.clear();
    this.deadExplosions.clear();
    this.deadLasers.clear();
  }

  //Increases wave number and the max UFOs per wave when called. 
  public void startNextWave() {
    this.waveNum++;
    GUI.displayWaveNum();
    GUI.setWaveNum(this.waveNum); 
    this.UFOsLeft = this.waveNum * this.UFOsInWave;
  }


  public void startBossFight() {
    if (this.mothership == null) {
      this.spawnMothership();
      this.UFOsLeft = 1;
    }
  }

  //Updates every object within the activeObjects lists. Also updates the GUI and score. 
  public void updateAllObjects() {
    for (Background b : this.backgroundLayers) { 
      b.update();
    }
    for (Upgrade u : this.activeUpgrades) { 
      u.update();
    }
    if (this.active) {
      rover.update();
      for (Bullet b : this.activeFriendlyBullets) { 
        b.update();
      } 
      for (Bomb b : this.activeBombs) { 
        b.update();
      }
      for (UFO u : this.activeUFOs) { 
        u.update();
      }
      for (Explosion e : this.activeExplosions) { 
        e.updateParticles();
      }
      for (Bullet b : this.activeEnemyBullets) {
        b.update();
      }
    }
    this.score.update();
    this.GUI.update();
  }


  public void spawnMothership() {
    this.mothership = new Mothership(this.color4, 0, 0);
    this.activeUFOs.add(this.mothership);
  }

  public void spawnUFO() {
    float rnd = random(100);
    //Special UFOs will spawn after round 1. Probability of a special UFO is a fraction of overall UFO probability. 
    if (this.waveNum > 1 && rnd <= this.probUFO / 10) {
      this.spawnSpecialUFO();
      return;
    }

    //Spawns regular UFO.
    if (rnd <= this.probUFO) {
      this.activeUFOs.add(new BombUFO(this.color4, width, random(height/8, height/3)));

      this.probUFO += this.increaseProbUFO;
    }
  }

  //Called by Spawn UFO. A special UFO uses weapons other than bombs. Can be distinguished by a different color.  
  public void spawnSpecialUFO() {
    float i = random(2);
    if (i > 1 && this.waveNum > 2) { 
      this.activeUFOs.add(new LaserUFO(this.color4, width, random(height/8, height/3)));
      return;
    }
    if (i > 0) { 
      this.activeUFOs.add(new GunnerUFO(this.color4, width, random(height/8, height/3)));
    }
  }


  public void removeUFO(UFO u) { 
    this.deadUFOs.add(u);
    this.UFOsLeft--;
  }

  public void removeEnemyBullet(Bullet b) {
    this.deadEnemyBullets.add(b);
  }

  public void removeBomb(Bomb b) { 
    this.deadBombs.add(b);
  }

  public void removeLaser(Laser l) {
    this.deadLasers.add(l);
  }

  public void removeMothership(Mothership m) {
    this.deadUFOs.add(m);
    this.mothership = null;
    this.startNextWave();
  }

  public Rover getRover() { 
    return this.rover;
  }

  public Mothership getMothership() {
    if (this.mothership != null) {
      return this.mothership;
    }
    return null;
  }

  public int getLives() { 
    return this.livesLeft;
  }

  public int getWave() {
    return this.waveNum;
  }

  public int getUFOsLeft() {
    return this.UFOsLeft;
  }

  //Sets active to false, which prevents activeObjects from being updated and displayed. 
  //Sets lostGame to true, which makes the GUI display the game over screen. 

  public void loseGame() {
    this.active = false;
    this.lostGame = true;
  }
}
class Mothership extends UFO {
  float bombSize = 30;
  float bombSpeed = 10;
  float scoreValue = 10000;
  float baseHeight = 150;
  boolean completedIntro = false;
  float lastDroppedBomb = millis();
  float bombCooldown = 400;
  boolean dying = false;
  ArrayList<ExplodingMothership> explosions = new ArrayList<ExplodingMothership>();
  ArrayList<ExplodingMothership> deadExplosions = new ArrayList<ExplodingMothership>();
  float lastExploded = millis();
  float explosionCooldown = 100;
  MothershipHealthBar healthBar;

  Mothership(int[] colorValues, float x, float y) {
    super(colorValues, x, y);
    this.w = 1000;
    this.h = 200;
    this.health = 100;
    this.scoreValue = 10000;
    this.speed = 2;
    this.UFOBody = allImages.get("Mothership");
    this.completedIntro = false;
    this.startAtTop();
  }

  public void startAtTop() {
    this.position = new PVector(width/2 - this.w/2, 0 - this.h);
  }

  //Increases the motherships y until it is at its base y. Once this happens, the mothership becomes active and can be interacted with. 
  public void enterScreen() {
    if (this.position.y < this.baseHeight) {
      this.position.y += 2;
    } else { 
      this.completedIntro = true;
      this.healthBar = new MothershipHealthBar(this.position.x + this.w/2, this.position.y - 25, this.position.x + this.w, this.health);
    }
  }

  public void update() {
    this.display();
    if (!this.completedIntro) {
      this.enterScreen();
      return;
    }
    if (this.dying) {
      this.playDyingAnimation();
      return;
    }
    this.healthBar.update();
    this.checkCollisions();
    this.checkOutOfBounds();
    this.dropBomb();
    this.position.x += this.speed;
    this.healthBar.move(this.speed);
  }


  //Bounces off edge of screen. 
  public void checkOutOfBounds() {
    if (this.position.x < 0) {
      this.position.x = 0;
      this.speed = this.speed * -1;
    }
    if (this.position.x + this.w > width) {
      this.position.x  = width - this.w;
      this.speed = this.speed * -1;
    }
  }

  public @Override
    void checkCollisions() {
    for (Bullet b : main.activeFriendlyBullets) {
      if (b.collides(this.position.x, this.position.y, this.w, this.h)) {
        main.deadFriendlyBullets.add(b);
        this.loseHealth();
        main.activeExplosions.add(new Explosion(this.colorValues, b.position, 3));
      }
    }
  }

  //Drops a bomb when above the rover. 
  public void dropBomb() {
    if (millis() - this.lastDroppedBomb > this.bombCooldown) {
      if (this.position.x < main.rover.getX()
        && this.position.x + this.w > main.rover.getX() + main.rover.getWidth()) {
        main.activeBombs.add(new Bomb(random(this.position.x, this.position.x + this.w), this.position.y + this.bombSize, this.bombSize, this.bombSpeed));
        this.lastDroppedBomb = millis();
      }
    }
  }


  public @Override
    void loseHealth() {
    if (this.hasHealthLeft()) {
      this.health--;
      this.healthBar.loseHealth();
    } else {
      this.destroyUFO();
    }
  }


  //Drops 3 active upgrades instead of 1. Has a dying animation which is turned on when destroyUFO() is called. 
  public @Override
    void destroyUFO() {
    if (!this.dying) { 
      for (int i = 0; i < 3; i++) {
        main.activeUpgrades.add(new Upgrade(random(this.position.x, this.position.x + this.w), random(this.position.y, this.position.y + this.h)));
      }
      main.score.increaseScore(this.scoreValue);
      this.dying = true;
    }
  }


  //Animates explosions around the Mothership as the mothership leaves the screen. Once the mothership exits the screen, it is deleted. 
  public void playDyingAnimation() {
    this.explosions.removeAll(this.deadExplosions);
    this.position.y -= 2;
    if (millis() - this.lastExploded > this.explosionCooldown) {
      this.explosions.add(new ExplodingMothership(random(this.position.x, this.position.x + this.w), random(this.position.y, this.position.y + this.h)));
      this.lastExploded = millis();
    }
    for (ExplodingMothership e : this.explosions) {
      e.update();
    }
    if (this.position.y + this.h + 100 < 0) {
      main.removeMothership(this);
    }
  }

  //Clears all completed explosions. 

  public void removeExplosion(ExplodingMothership e) {
    this.deadExplosions.add(e);
  }
}
class MothershipHealthBar {
  PVector position = new PVector(0 , 0);
  float maxHealth;
  float healthLeft; 
  float h = 20;
  float baseW = 500;
  
  MothershipHealthBar(float x, float y, float baseW, float health){
    this.position.x = x;
    this.position.y = y;
    this.healthLeft = health;
    this.maxHealth = health;
  }
  
  
  //Displays a green health bar above the mothership. 
  public void update(){
    fill(0, 255, 0);
    noStroke();
    rectMode(CENTER);
    rect(this.position.x, this.position.y, (this.baseW / this.maxHealth) * this.healthLeft, this.h);
  }
  
  public void move(float x){
    this.position.x += x;
  }
  
  public void loseHealth(){
    this.healthLeft--;
  }
}
class Particle {
  PVector position = new PVector(0, 0);
  PVector velocity = new PVector(0, 0);
  PVector acceleration = new PVector(0, 0);
  float lifespan;
  int[] colorValues;

//Particles are created by the explosion class when destroying basic UFOs or damaging the mothership. 
  Particle(int[] colorValues, PVector l) {
    this.colorValues = colorValues;
    this.acceleration = new PVector(0, 0.7f);
    this.velocity = new PVector(random(-5, 5), random(-10, 5));
    this.position = l.copy();
    this.lifespan = 255.0f;
  }

  public void update() {
    this.velocity.add(this.acceleration);
    this.position.add(this.velocity);
    lifespan -= 1.0f;
  }

  public void display() {
    noStroke();
    fill(255, this.lifespan); 
    ellipse(position.x, position.y, 10, 10);
  }

  public boolean isDead() {
    if (this.lifespan < 0) {
      return true;
    } else {
      return false;
    }
  }
}
class Rover {
  int[] colorValues;
  PImage roverBody;
  PVector position = new PVector(0, 0);
  PVector cannonBase = new PVector(0, 0);
  PVector cannonEnd = new PVector(0, 0);
  float w = 150;
  float h = 50;
  float cannonLength = 100;
  float ouchOpacity = 0;

  //Stats
  float speed = 10;
  float bulletSpeed = 15;
  float fireRate = 10;

  boolean firing = false;
  float firingCounter = 0; 

  Rover(int[] colorValues, float x, float y) {
    this.colorValues = colorValues;
    this.position = new PVector(x, y);
    this.position.x = x;
    this.position.y = y;
    this.cannonBase.x = x + 20;
    this.cannonBase.y = y - 20;
    this.roverBody = allImages.get("Rover");
  }

  public void update() {
    this.checkCollisions();
    this.drawRover();
    this.drawCannon();
    if (this.firing && this.canShoot()) {  
      this.shoot();
    }
    if(this.ouchOpacity > 0){
      this.drawOuch();
    }
  }
  
  //Displays test saying "Ouch!" above the rover after taking damage".
  public void drawOuch(){
      fill(255, 255, 255, this.ouchOpacity);
      this.ouchOpacity = this.ouchOpacity - 2;
      textSize(50);
      textAlign(CENTER, CENTER);
      text("OUCH!", this.position.x + (this.w / 2), this.position.y - 80);
    
  }

  public void drawRover() {
    noStroke();
    fill(colorValues[0], colorValues[1], colorValues[2]);
    tint(colorValues[0], colorValues[1], colorValues[2]);
    image(roverBody, this.position.x - 20, this.position.y - 50);
  }


//Draws a line to represent the cannon. The line is directed at the mouse. 
  public void drawCannon() {
    //With help from https://wiki.processing.org/examples/vectormath.html
    pushMatrix();
    PVector mouse = new PVector(mouseX, mouseY);
    this.cannonEnd = new PVector(cannonBase.x, cannonBase.y);
    mouse.sub(this.cannonBase);
    mouse.normalize();
    mouse.mult(this.cannonLength);
    translate(cannonBase.x, cannonBase.y);
    stroke(colorValues[0], colorValues[1], colorValues[2]);
    strokeWeight(15);
    line(0, 0, mouse.x, mouse.y);
    popMatrix();
  }



//Shoots a bullet from the end of the cannon in the direction of the mouse. 
  public void shoot() {
    //With help from http://studio.processingtogether.com/sp/pad/export/ro.91kLmk61vAOZp/latest
    PVector bulletVector = new PVector(mouseX, mouseY);
    pushMatrix();
    //Help from tutor
    PVector newVector = cannonBase.copy().add(bulletVector.copy().sub(cannonBase).normalize().mult(this.cannonLength));
    bulletVector.sub(this.cannonBase);
    bulletVector.normalize();
    bulletVector.mult(this.bulletSpeed);
    Bullet b = new Bullet(this.colorValues, newVector, bulletVector);
    main.activeFriendlyBullets.add(b);
    popMatrix();
  }


//Controls the fire rate.
  public boolean canShoot() {
    if (this.firingCounter >= this.fireRate) {
      this.firingCounter = 0;
      return true;
    } else {
      this.firingCounter++;
      return false;
    }
  }


  public void move(int direction) {
    this.position.x += direction * this.speed;
    this.cannonBase.x += direction * this.speed;
    this.checkOutOfBounds();
  }


//Ensures rover can't drive off screen. 
  public void checkOutOfBounds() {
    if (this.position.x < 0) {
      this.position.x += this.speed;
      this.cannonBase.x += this.speed;
    }
    if (this.position.x + this.w > width) {
      this.position.x -= this.speed;
      this.cannonBase.x -= this.speed;
    }
  }

  public boolean collides(float otherX, float otherY, float otherW, float otherH) {
    if (this.position.x + this.w > otherX
      && this.position.x < otherX + otherW
      && this.position.y + this.h > otherY
      && this.position.y < otherY + otherH) {
      return true;
    }  
    return false;
  }

//Checks to see if the rover collides with any active projectiles. 

  public void checkCollisions() {
    if (main.activeBombs.size() > 0) {
      for (Bomb b : main.activeBombs) {
        if(this.collides(b.getX(), b.getY(), b.getSize(), b.getSize())){
          this.ouchOpacity = 255;
          main.GUI.healthBar.loseLife();
        }
        
      }
    }
    if (main.activeLasers.size() > 0) {
      for (Laser l : main.activeLasers) {
        if(this.collides(l.getX(), l.getY(), l.getWidth(), l.getHeight())){
          this.ouchOpacity = 255;
         main.GUI.healthBar.loseLife(); 
        }
      }
    }
    
    if(main.activeEnemyBullets.size() > 0){
      for(Bullet b : main.activeEnemyBullets){
        if(this.collides(b.getX(), b.getY(), b.getSize(), b.getSize())){
          main.GUI.healthBar.loseLife();
          this.ouchOpacity = 255;
          main.deadEnemyBullets.add(b);
        }
      }
    }
  }
  
  //Getter and Setter methods.

  public float getX() { 
    return this.position.x;
  }
  public float getY() { 
    return this.position.y;
  }
  public float getWidth() { 
    return this.w;
  }
  public float getHeight() { 
    return this.h;
  }

  public float getSpeed() { 
    return this.speed;
  } 
  public void setSpeed(float speed) { 
    this.speed = speed;
  }

  public float getFireRate() { 
    return this.fireRate;
  }
  public void setFireRate(float rate) { 
    this.fireRate = rate;
  }

  public float getBulletSpeed() { 
    return this.bulletSpeed;
  }
  public void setBulletSpeed(float speed) { 
    this.bulletSpeed = speed;
  }

  public void increaseFireRate() {
    this.fireRate = this.fireRate * 0.9f;
  }

  public void increaseSpeed() {
    this.speed +=0.6f;
  }

  public void increaseBulletSpeed() {
    this.bulletSpeed +=1;
  }

}
class Score {
  float currentScore;

  Score() {
    this.currentScore = 0;
  }

//Hides the score when the game isn't active. 
  public void update() {
    if (main.active) {
      this.display();
    }
  }


//Shows the score up the top left of the screen. 
  public void display() {
    fill(255);
    textSize(50);
    textAlign(CORNER);
    text("Score: " + round(this.currentScore), 10, 120);
  }

  public void increaseScore(float scoreToAdd) {
    this.currentScore += scoreToAdd;
  }

  public float getCurrentScore() {
    return this.currentScore;
  }
}
class UFO {
  int[]colorValues;
  PImage UFOBody;
  PVector position = new PVector(0, 0);
  float w = 200;
  float h = 50;
  float speed = 10;
  int health = 1;
  int ammo = 1;
  int maxAmmo = 1;
  float bombSize = 30;
  float bombSpeed = 10;
  float scoreValue = 100;

  UFO(int[] colorValues, float x, float y) {
    this.UFOBody = allImages.get("BombUFO"); 
    this.colorValues = colorValues;
    this.position.x = x;
    this.position.y = y;
    int[] directions = {-1, 1};
    int direction = PApplet.parseInt(random(directions.length));
    this.speed = this.speed * directions[direction];
  }

  public void update() {
    this.checkCollisions();
    this.display();
    this.checkOutOfBounds();
    position.x += speed;
  }
  
  
//Checks if the UFO is still on screen. Moves the UFO to the opposite side of the screen when out of bounds. 
  public void checkOutOfBounds() {
    if (this.position.x + this.w < 0) {
      this.position.x = width;
      this.ammo = maxAmmo;
    }

    if (this.position.x > width) {
      this.position.x = 0 - this.w;
      this.ammo = maxAmmo;
    }
  }

  public void display() {
    fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    tint(colorValues[0], colorValues[1], colorValues[2]);
    noStroke();
    image(this.UFOBody, this.position.x, this.position.y);
  }

//Checks to see if the UFO collides with any projectiles. 
  public void checkCollisions() {
    for (Bullet b : main.activeFriendlyBullets) {
      if (b.collides(this.position.x, this.position.y, this.w, this.h)) {
        main.deadFriendlyBullets.add(b);
        this.loseHealth();
      }
    }
  }

  public void loseHealth() {
    if (this.hasHealthLeft()) {
      this.health--;
    } else {
      this.destroyUFO();
    }
  }


//Removes the UFO from the game. Creates an explosion object where destroyed. Increases score.
//Has a chance to drop an upgrade. 

  public void destroyUFO() {
    main.removeUFO(this);
    main.activeExplosions.add(new Explosion(this.colorValues, this.position, 10));
    main.score.increaseScore(this.scoreValue);
    
    float rnd = random(100);
    if (rnd <= main.probUpgrade) {
      main.activeUpgrades.add(new Upgrade(this.position.x, this.position.y));
    }
  }

  public boolean hasHealthLeft() {
    return(this.health > 0);
  }

  public float getX() {
    return this.position.x;
  }

  public float getY() {
    return this.position.y;
  }

  public float getWidth() {
    return this.w;
  }

  public float getHeight() {
    return this.h;
  }
}
class Upgrade {
  PVector position;
  float velocity = 5;
  String[] possibleUpgrades = {"Speed", "Fire Rate", "Bullet Speed"};
  String name;
  float size = 50;

  Upgrade(float x, float y) {
    this.position = new PVector(x, y);
    //Randomly selects an upgrade from the array of possible upgrades. 
    this.name = possibleUpgrades[(int) random(0, possibleUpgrades.length)];    
  }
//Checks if the upgrade collides with the rover. Otherwise, moves the upgrade and displays it. 
  public void update() {
    if (main.getRover().collides(this.position.x, this.position.y, this.size, this.size)) {
      main.deadUpgrades.add(this);
      this.applyUpgrade();
    }
    this.move();
    this.display();
  }

  public void move() {
    if (this.position.y < main.groundY + 50) {
      this.position.y += velocity;
    }
  }

  public void display() {
    fill(0, 255, 0);
    ellipseMode(CENTER);
    ellipse(this.position.x, this.position.y, this.size, this.size);
  }


//Upgrades the Rover depending on the upgrades name assigned in the constructor. 
  public void applyUpgrade() {
    if (this.name.equals("Speed")) { 
      main.getRover().increaseSpeed();
      main.GUI.setUpgrade("Speed");
    } else if (this.name.equals("Fire Rate")) {
      main.getRover().increaseFireRate();
     main.GUI.setUpgrade("Fire Rate");
    } else if (this.name.equals("Bullet Speed")) {
      main.getRover().increaseBulletSpeed();
      main.GUI.setUpgrade("Bullet Speed");
    }
  }
}
  public void settings() {  size(1920, 1080); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Curiositys_Conundrum" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

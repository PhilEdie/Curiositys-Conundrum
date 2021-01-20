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

  void update() {
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
  void displayControls() {
    if (this.controlsOpacity > 0) {
      fill(255, 255, 255, this.controlsOpacity);
      this.controlsOpacity = this.controlsOpacity - 0.5;
      textSize(100);
      textAlign(CENTER, CENTER);
      text("A and D to move, Mouse to shoot.", width/2, height/2 + 100);
    }
  }

  //Draws the most recent upgrade on screen. eg: "Fire Rate Increased"
  void displayUpgrade() {
    if (this.upgradeOpacity > 0) {
      fill(255, 255, 255, this.upgradeOpacity);
      this.upgradeOpacity--;
      textSize(100);
      textAlign(CENTER, CENTER);
      text(this.lastUpgrade + " Increased", width/2, height/2);
    }
  }



  //Shows how many UFOs are left in the wave up the top right of the screen. 
  void displayLeftInWave() {
    fill(255);
    textSize(50);
    textAlign(RIGHT, TOP);
    text("UFO's Remaining: " + (main.getUFOsLeft()), width -20, 10);
  }


  //Sets the upgrade to be displayed by displayUpgrade()
  void setUpgrade(String name) {
    this.lastUpgrade = name;
    this.upgradeOpacity = 255;
  }


  //Displays the current wave on screen. Called when the player moves to the next wave
  void displayWaveNum() {
    fill(255, 255, 255, this.waveNumOpacity);
    this.waveNumOpacity--;
    textSize(100);
    textAlign(CENTER, CENTER);
    text("Wave " + (this.lastWaveNum), width/2, height/2);
  }

  void setWaveNum(int wave) {
    this.lastWaveNum = wave;
    this.waveNumOpacity = 255;
  }

  //Game over screen. Shows the last wave, the score, and how to play again
  void drawLostGame() {
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

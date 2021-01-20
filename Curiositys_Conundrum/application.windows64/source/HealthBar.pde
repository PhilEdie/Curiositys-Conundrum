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

  void update() {
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
  void loseLife() {
    if (millis() - lastLostLife > this.invincibleTime
      && this.livesLeft > 0) {
      this.livesLeft--;
      this.lastLostLife = millis();
    }
  }
}

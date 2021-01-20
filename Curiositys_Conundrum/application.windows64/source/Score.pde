class Score {
  float currentScore;

  Score() {
    this.currentScore = 0;
  }

//Hides the score when the game isn't active. 
  void update() {
    if (main.active) {
      this.display();
    }
  }


//Shows the score up the top left of the screen. 
  void display() {
    fill(255);
    textSize(50);
    textAlign(CORNER);
    text("Score: " + round(this.currentScore), 10, 120);
  }

  void increaseScore(float scoreToAdd) {
    this.currentScore += scoreToAdd;
  }

  float getCurrentScore() {
    return this.currentScore;
  }
}

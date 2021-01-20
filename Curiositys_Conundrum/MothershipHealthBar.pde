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
  void update(){
    fill(0, 255, 0);
    noStroke();
    rectMode(CENTER);
    rect(this.position.x, this.position.y, (this.baseW / this.maxHealth) * this.healthLeft, this.h);
  }
  
  void move(float x){
    this.position.x += x;
  }
  
  void loseHealth(){
    this.healthLeft--;
  }
}

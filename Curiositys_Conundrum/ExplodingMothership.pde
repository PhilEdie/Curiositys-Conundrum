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
  
  
  void update(){
    this.size += this.increaseSize;
    if(this.size >= this.maxSize){
      main.getMothership().removeExplosion(this);
    }
    fill(255);
    noStroke();
    ellipse(this.position.x, this.position.y, this.size, this.size);
  }
}

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
  void update(float speed){
    this.position.x += speed;
    this.w += 1;
    fill(255);
    rectMode(CORNER);
    rect(this.position.x - w/2, this.position.y + 6, w, height);
    this.lifeTime--;
  }
  
  float getX(){
    return this.position.x;
  }
  
  float getY(){
   return this.position.y; 
  }
  
  float getWidth(){
   return this.w; 
  }
  
  float getHeight(){
    return height;    
  }
  
  float getLifeTime(){
    return this.lifeTime;
  }

  boolean isComplete(){
    return this.lifeTime <= 0;
  }
}

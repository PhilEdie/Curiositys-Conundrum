HashMap<String, PImage> allImages = new HashMap<String, PImage>();
Main main;
int keyAmount = 128;

void setup() {
  this.importImages();
  main = new Main();
  size(1920, 1080);
  stroke(255);
}

void draw() {
  main.update();
}

void keyPressed() {
  //Prevents a crash when pressing shift + any key
  if (keyCode > keyAmount) {
    keyCode = '0';
  }
  main.keys[keyCode] = true;
  
  //Pressing enter restarts the game. 
  if(key == ENTER){ main = new Main(); }
}


void keyReleased() {
  //Prevents a crash when pressing shift + any key
  if (keyCode > keyAmount) {
    keyCode = '0';
  }

  main.keys[keyCode] = false;
}

void mousePressed() {
  main.getRover().firing = true;
}

void mouseReleased() {
  main.getRover().firing = false;
}


void importImages(){
  this.allImages.put("Mothership", loadImage("MothershipBody.png"));
  this.allImages.put("Rover", loadImage("RoverBody.png"));
  this.allImages.put("BombUFO", loadImage("UFOBody.png"));
  this.allImages.put("LaserUFO", loadImage("LaserUFOBody.png"));
  this.allImages.put("GunnerUFO", loadImage("GunnerUFOBody.png"));
}

class Background {
  float animSpeed = 1;
  float waitedFrames = 0; 
  float baseY;
  int[] colorValues;
  float xoff = 0.0;
  float yoff = 0.0;
  float xOffIncrement = 0.01;
  float yOffIncrement = 0.1;
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
  void update() {
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

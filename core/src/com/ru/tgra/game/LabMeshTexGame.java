package com.ru.tgra.game;


import java.util.Random;
import java.util.TimerTask;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.ru.tgra.graphics.*;
import com.ru.tgra.graphics.shapes.*;
import com.ru.tgra.graphics.shapes.g3djmodel.G3DJModelLoader;
import com.ru.tgra.graphics.shapes.g3djmodel.MeshModel;
import com.ru.tgra.graphics.Maze;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.Timer;

public class LabMeshTexGame extends ApplicationAdapter implements InputProcessor {

	Shader shader;

	private float angle;

	private float moveLength;
	private float moveLength2;
	private float speed;

	private Camera cam;
	private Camera topCam;
	private float delay;
	BlinkingLights copLights;
	private boolean lightsOnOff;
	
	
	TimerTask lightTask;
	
	public static int colorLoc;
	
	private float fov = 90.0f;

	MeshModel model;
	MeshModel gem;

	private Texture tex;
	private Texture congratz;
	
	private Maze maze;
	private boolean movingRight;
	private boolean movingUp;
	private boolean showGem;
	
	private Sound scream;
	private Sound coin;
	
	Random rand = new Random();

	@Override
	public void create () {
		movingRight = true;
		movingUp = true;
		showGem = true;

		Gdx.input.setInputProcessor(this);

		DisplayMode disp = Gdx.graphics.getDesktopDisplayMode();
		Gdx.graphics.setDisplayMode(disp.width, disp.height, true);

		shader = new Shader();
		maze = new Maze(15, 15);
		
		delay = 1;
		Timer timer;
		copLights = new BlinkingLights();
		lightsOnOff = true;

		tex = new Texture(Gdx.files.internal("textures/trump.jpg"));
		congratz = new Texture(Gdx.files.internal("textures/congratz.png"));

		model = G3DJModelLoader.loadG3DJFromFile("finalGhostRotate2.g3dj");
		gem = G3DJModelLoader.loadG3DJFromFile("gemRotate1.g3dj");
		
		BoxGraphic.create();
		SphereGraphic.create();

		ModelMatrix.main = new ModelMatrix();
		ModelMatrix.main.loadIdentityMatrix();
		shader.setModelMatrix(ModelMatrix.main.getMatrix());

		cam = new Camera();
		cam.look(new Point3D(1.5f, 1f, -0.5f), new Point3D(2.5f,1,-1.5f), new Vector3D(0,1,0));
		topCam = new Camera();
		topCam.orthographicProjection(-10, 10, -10, 10, 3.0f, 100);	

		Gdx.gl.glClearColor(0.35f, 0.35f, 0.8f, 1.0f);
		Gdx.input.setCursorCatched(true);
		
		speed = 3.5f;
		moveLength = 0.0f;
		moveLength2 = 0.0f;
		
		scream = Gdx.audio.newSound(Gdx.files.internal("scream.mp3"));
		coin = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
	}

	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		angle += 180.0f * deltaTime;
		
		if(movingRight) {
			moveLength += speed * deltaTime;
		} else {
			moveLength -= speed * deltaTime;
		}
		
		if (movingUp) {
			moveLength2 += speed * deltaTime;
		}
		else {
			moveLength2 -= speed * deltaTime;
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			cam.slide(-3.0f * deltaTime, 0, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			cam.slide(3.0f * deltaTime, 0, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			cam.walkForward(3.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			cam.walkForward(-3.0f * deltaTime);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.O)) {
			lightsOnOff = !lightsOnOff;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cam.rotateY(90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cam.rotateY(-90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			cam.pitch(90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			cam.pitch(-90.0f * deltaTime);
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.graphics.setDisplayMode(500, 500, false);
			scream.dispose();
			Gdx.app.exit();
		}
		
		cam.rotateY(-0.2f * Gdx.input.getDeltaX());
		cam.pitch(-0.2f * Gdx.input.getDeltaY());	
	}
	
	private void display()
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glUniform4f(LabMeshTexGame.colorLoc, 1.0f, 0.3f, 0.1f, 1.0f);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		for(int viewNum = 0; viewNum < 2; viewNum++)
		{
			if(viewNum == 0)
			{
				Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
				cam.perspectiveProjection(fov, (float)Gdx.graphics.getWidth() / (float)(2*Gdx.graphics.getHeight()), 0.2f, 100.0f);
				shader.setViewMatrix(cam.getViewMatrix());
				shader.setProjectionMatrix(cam.getProjectionMatrix());
				shader.setEyePosition(cam.eye.x, cam.eye.y, cam.eye.z, 1.0f);
			}
			else
			{
				Gdx.gl.glViewport(Gdx.graphics.getWidth() / 2, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
				topCam.look(new Point3D(7.0f, 40.0f, -7.0f), new Point3D(7.0f, 0.0f, -7.0f), new Vector3D(0,0,-1));
				topCam.perspectiveProjection(30.0f, (float)Gdx.graphics.getWidth() / (float)(2*Gdx.graphics.getHeight()), 3, 100);
				shader.setViewMatrix(topCam.getViewMatrix());
				shader.setProjectionMatrix(topCam.getProjectionMatrix());
				shader.setEyePosition(topCam.eye.x, topCam.eye.y, topCam.eye.z, 1.0f);	
			}
			
			ModelMatrix.main.loadIdentityMatrix();
			
			float s = (float)Math.sin((angle / 2.0) * Math.PI / 180.0);
			float c = (float)Math.cos((angle / 2.0) * Math.PI / 180.0);

			if (lightsOnOff == true) {
				shader.setLightPosition2(15 * s + 10.0f, 7.0f,15 * c -10.0f, 1.0f);
				if (copLights.getBlueOrRed()) {
					shader.setLightColor2(0.2f, 0.2f, 1.0f, 1.0f);
				}
				else {
					shader.setLightColor2(1.0f, 0.2f, 0.2f, 1.0f);
				}
			}
			else {
				shader.setLightColor2(1, 1, 1, 1.0f);
				shader.setLightPosition2(0.0f, 5.0f, 0.0f, 1.0f);
			}

			float s2 = Math.abs((float)Math.sin((angle / 1.312) * Math.PI / 180.0));
			float c2 = Math.abs((float)Math.cos((angle / 1.312) * Math.PI / 180.0));

			shader.setSpotDirection(s2, -0.3f, c2, 0.0f);
			shader.setSpotExponent(0.0f);
			shader.setConstantAttenuation(1.0f);
			shader.setLinearAttenuation(0.00f);
			shader.setQuadraticAttenuation(0.00f);
			
			shader.setGlobalAmbient(0.3f, 0.3f, 0.3f, 1);

			shader.setMaterialDiffuse(1.0f, 1.0f, 1.0f, 1.0f);
			shader.setMaterialSpecular(1.0f, 1.0f, 1.0f, 1.0f);
			shader.setMaterialEmission(0, 0, 0, 1);
			shader.setShininess(50.0f);
			
			if (viewNum == 0) {
				drawHorizontalBlobs();
				drawVerticalBlobs();
			}

			//Gem
			if (showGem == true) {
				drawGems();
				if (Maze.cells[11][13].northWall == false) {
					Maze.cells[11][13].northWall = true;
				}
			}
			if(showGem == false) {
				Maze.cells[11][13].northWall = false;
			}

			//Trump Box
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(8.0f, 6.0f, -8.0f);
			ModelMatrix.main.addScale(2.0f, 2.0f, 2.0f);
			ModelMatrix.main.addRotation(angle, new Vector3D(1,1,1));
			shader.setModelMatrix(ModelMatrix.main.getMatrix());
			BoxGraphic.drawSolidCube(shader, tex);
			ModelMatrix.main.popMatrix();
			
			//'Congratulations' box
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(12.0f, 1.5f, -16.0f);
			ModelMatrix.main.addScale(2.0f, 2.0f, 0.1f);
			shader.setModelMatrix(ModelMatrix.main.getMatrix());
			BoxGraphic.drawSolidCube(shader, congratz);
			ModelMatrix.main.popMatrix();
	
			shader.setMaterialDiffuse(0.5f, 0.3f, 1.0f, 1.0f);
			shader.setMaterialSpecular(1.0f, 1.0f, 1.0f, 1.0f);
			shader.setMaterialEmission(0, 0, 0, 1);
			shader.setShininess(50.0f);
			maze.drawMaze();
			
			if(viewNum == 1)
			{				
				ModelMatrix.main.pushMatrix();
				ModelMatrix.main.addTranslation(cam.eye.x, cam.eye.y, cam.eye.z);

				ModelMatrix.main.addScale(0.25f, 0.25f, 0.25f);
				shader.setModelMatrix(ModelMatrix.main.getMatrix());
				SphereGraphic.drawSolidSphere(shader, null, null);

				ModelMatrix.main.popMatrix();				
			}
		}
	}
	
	public void checkCollision(Point3D p) {
		if( (cam.eye.x > (p.x - 0.5f) && cam.eye.x < (p.x + 0.5f)) &&
			(cam.eye.z > (p.z - 0.5f) && cam.eye.z < (p.z + 0.5f)) ) 
			{
				scream.setVolume(scream.play(), 0.4f);
				cam.look(new Point3D(1.5f, 1f, -0.5f), new Point3D(2.5f,1,-1.5f), new Vector3D(0,1,0));
				showGem = true;
			}
	}
	
	public void checkGemCollision(Point3D p) {
		if( (cam.eye.x > (p.x - 0.5f) && cam.eye.x < (p.x + 0.5f)) &&
			(cam.eye.z > (p.z - 0.5f) && cam.eye.z < (p.z + 0.5f)) ) 
			{
				coin.setVolume(coin.play(), 0.4f);
				showGem = false;
			}
	}
	
	public void drawGems() {
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(5.5f, 1.0f, -8.5f);
		checkGemCollision(ModelMatrix.main.getOrigin());
		ModelMatrix.main.addScale(0.3f, 0.3f, 0.3f);
		ModelMatrix.main.addRotation(angle, new Vector3D(1,1,1));
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		gem.draw(shader);
		ModelMatrix.main.popMatrix();
	}
	
	public void drawHorizontalBlobs() {
		//Ghost 1
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(1.5f + moveLength, 1.0f, -4.5f);
		if(ModelMatrix.main.getOrigin().x > 13.5f && movingRight == true) {
			movingRight = false;
		}
		if(ModelMatrix.main.getOrigin().x < 1.4f && movingRight == false) {
			movingRight = true;
		}
		checkCollision(ModelMatrix.main.getOrigin());
		ModelMatrix.main.addScale(0.25f, 0.25f, 0.25f);
		ModelMatrix.main.addRotation(angle, new Vector3D(0,1,0));
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		model.draw(shader);
		ModelMatrix.main.popMatrix();
		
		//Ghost 2
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(1.5f + moveLength, 1.0f, -8.5f);
		if(ModelMatrix.main.getOrigin().x > 13.5f && movingRight == true) {
			movingRight = false;
		}
		if(ModelMatrix.main.getOrigin().x < 1.4f && movingRight == false) {
			movingRight = true;
		}
		checkCollision(ModelMatrix.main.getOrigin());
		ModelMatrix.main.addScale(0.25f, 0.25f, 0.25f);
		ModelMatrix.main.addRotation(angle, new Vector3D(0,1,0));
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		model.draw(shader);
		ModelMatrix.main.popMatrix();
		
		//Ghost 3
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(1.5f + moveLength, 1.0f, -12.5f);
		if(ModelMatrix.main.getOrigin().x > 13.5f && movingRight == true) {
			movingRight = false;
		}
		if(ModelMatrix.main.getOrigin().x < 1.4f && movingRight == false) {
			movingRight = true;
		}
		checkCollision(ModelMatrix.main.getOrigin());
		ModelMatrix.main.addScale(0.25f, 0.25f, 0.25f);
		ModelMatrix.main.addRotation(angle, new Vector3D(0,1,0));
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		model.draw(shader);
		ModelMatrix.main.popMatrix();
	}

	public void drawVerticalBlobs() {
		//Ghost 1
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(5.5f, 1.0f, -1.5f - moveLength2);
		if(ModelMatrix.main.getOrigin().z < -12.5f && movingUp == true) {
			movingUp = false;
		}
		if(ModelMatrix.main.getOrigin().z > -0.5f && movingUp == false) {
			movingUp = true;
		}
		checkCollision(ModelMatrix.main.getOrigin());
		ModelMatrix.main.addScale(0.25f, 0.25f, 0.25f);
		ModelMatrix.main.addRotation(angle, new Vector3D(0,1,0));
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		model.draw(shader);
		ModelMatrix.main.popMatrix();
		
		//Ghost 2
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(9.5f, 1.0f, -1.5f - moveLength2);
		if(ModelMatrix.main.getOrigin().z < -12.5f && movingUp == true) {
			movingUp = false;
		}
		if(ModelMatrix.main.getOrigin().z > -0.5f && movingUp == false) {
			movingUp = true;
		}
		checkCollision(ModelMatrix.main.getOrigin());
		ModelMatrix.main.addScale(0.25f, 0.25f, 0.25f);
		ModelMatrix.main.addRotation(angle, new Vector3D(0,1,0));
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		model.draw(shader);
		ModelMatrix.main.popMatrix();
		
		//Ghost 3
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(13.5f, 1.0f, -1.5f - moveLength2);
		if(ModelMatrix.main.getOrigin().z < -12.5f && movingUp == true) {
			movingUp = false;
		}
		if(ModelMatrix.main.getOrigin().z > -0.5f && movingUp == false) {
			movingUp = true;
		}
		checkCollision(ModelMatrix.main.getOrigin());
		ModelMatrix.main.addScale(0.25f, 0.25f, 0.25f);
		ModelMatrix.main.addRotation(angle, new Vector3D(0,1,0));
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		model.draw(shader);
		ModelMatrix.main.popMatrix();
	}
	
	@Override
	public void render () {
		update();
		display();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}


}
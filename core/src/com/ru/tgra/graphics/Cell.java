package com.ru.tgra.graphics;

import com.badlogic.gdx.Gdx;
import com.ru.tgra.graphics.shapes.BoxGraphic;
import com.ru.tgra.game.LabMeshTexGame;
import com.ru.tgra.graphics.ModelMatrix;
import com.ru.tgra.graphics.Shader;

public class Cell {
	Shader shader;
	public boolean northWall;
	public boolean eastWall;
	public float wallWidth;
	
	public Cell(boolean north, boolean east){
		shader = new Shader();
		this.northWall = north;
		this.eastWall = east;
		wallWidth = 0.1f;
	}
	
	public void draw(){
		Gdx.gl.glUniform4f(LabMeshTexGame.colorLoc, 0.8f, 0.3f, 0.5f, 1.0f);
		if(northWall){
			ModelMatrix.main.pushMatrix();
				//Gdx.gl.glUniform4f(LabFirst3DGame.colorLoc, color, 0.3f, 0.1f, 1.0f);
				ModelMatrix.main.addTranslation(0.5f, 0, 0);
				ModelMatrix.main.addScale(1.1f,1, wallWidth);
				//ModelMatrix.main.setShaderMatrix();
				shader.setModelMatrix(ModelMatrix.main.getMatrix());
				BoxGraphic.drawSolidCube(shader, null);
	
			ModelMatrix.main.popMatrix();
	
		}
		if(eastWall){
			ModelMatrix.main.pushMatrix();
			//Gdx.gl.glUniform4f(LabFirst3DGame.colorLoc, color, 0.3f, 0.1f, 1.0f);
				ModelMatrix.main.addTranslation(1, 0, 0.5f);
				ModelMatrix.main.addScale(wallWidth,1, 1.1f);
				//ModelMatrix.main.setShaderMatrix();
				shader.setModelMatrix(ModelMatrix.main.getMatrix());
				BoxGraphic.drawSolidCube(shader, null);
				
			ModelMatrix.main.popMatrix();
		}
		Gdx.gl.glUniform4f(LabMeshTexGame.colorLoc, 1.0f, 0.3f, 0.1f, 1.0f);
	}
}

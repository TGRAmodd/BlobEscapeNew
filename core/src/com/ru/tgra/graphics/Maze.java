package com.ru.tgra.graphics;

import com.ru.tgra.graphics.shapes.BoxGraphic;
import com.ru.tgra.graphics.Cell;
import com.ru.tgra.graphics.MazeBuilder;
import com.ru.tgra.graphics.ModelMatrix;
import com.ru.tgra.graphics.Shader;

public class Maze {
	public static Cell[][] cells;
	public static int width;
	public static int height;
	
	Shader shader;
	
	public Maze(int width, int height){
		shader = new Shader();
		Maze.width = width;
		Maze.height = height;
		cells = new Cell[width][height];
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(i == 0){
					cells[i][j] = new Cell(false, true);
				}
				if(j == 0){
					cells[i][j] = new Cell(true, false);
				}
				if(i == width-1){
					cells[i][j] = new Cell(false, false);
				}
				if(j == height-1){
					cells[i][j] = new Cell(false, false);
				}
				if(i == width-2){
					cells[i][j] = new Cell(false,true);
				}
				if(j == height -2){
					cells[i][j] = new Cell(true, false);
				}
			}
		}
		if(width == 15 && height == 15){
			MazeBuilder mb = new MazeBuilder();
			mb.buildHardCodedMaze();
		}
	}
	
	public void drawMaze(){
		ModelMatrix.main.pushMatrix();
		
		//floor
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addTranslation(0, -50f, 0);
		ModelMatrix.main.addScale(50, 100f, 50f);
		
		
		//ModelMatrix.main.setShaderMatrix();
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		BoxGraphic.drawSolidCube(shader, null);
		ModelMatrix.main.popMatrix();
		
		ModelMatrix.main.addTranslation(0,1,0);
		for(int i = 0; i < width; i++){
			ModelMatrix.main.pushMatrix();
				for(int j = 0; j < height; j++){
					cells[i][j].draw();
					ModelMatrix.main.addTranslation(0, 0, -1);
				}
			ModelMatrix.main.popMatrix();
			ModelMatrix.main.addTranslation(1,0,0);
		}
		ModelMatrix.main.popMatrix();
	}
	public static Cell getNorth(int x, int z){
		if(z >= height-1){
			return null;
		}
		else{
			return cells[x][z+1];
		}
	}
	public static Cell getSouth(int x, int z){
		if(z <= 0){
			return null;
		}
		else{
			return cells[x][z-1];
		}
	}
	public static Cell getEast(int x, int z){
		if(x >= width-1){
			return null;
		}
		else{
			return cells[x+1][z];
		}
	}
	public static Cell getWest(int x, int z){
		if(x <= 0){
			return null;
		}
		else{
			return cells[x-1][z];
		}
	}
}

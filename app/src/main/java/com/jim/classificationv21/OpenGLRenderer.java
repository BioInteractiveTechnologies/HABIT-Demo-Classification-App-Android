package com.jim.classificationv21;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements Renderer {

        private Cube mCube = new Cube();
        private float mCubeRotation;
        RawDataActivity parent;

        double roll = 0;
        double pitch = 0;
        double yaw = 0;

        public OpenGLRenderer(RawDataActivity parent)
        {

        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 
                
            gl.glClearDepthf(1.0f);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);

            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                      GL10.GL_NICEST);
                
        }

        public void updateRotationValues(Double roll, Double pitch, Double yaw)
        {
            this.roll = roll;
            this.pitch = pitch;
            this.yaw = yaw;
            //Log.i("updateRotationValues",""+ roll + " " + pitch + " " + yaw);
        }
        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            
            gl.glTranslatef(0.0f, 0.0f, -5.0f);

            //jim
            //rotation
            //gl.glRotatef(1, (float)roll, (float)pitch, (float)yaw);
            gl.glRotatef((float)roll, 0.0f,0.0f,1.0f);
            gl.glRotatef((float)pitch, 1.0f,0.0f,0.0f);
            gl.glRotatef((float)yaw, 0.0f,1.0f,0.0f);
            //Log.i("onDrawFrame",""+ roll + " " + pitch + " " + yaw);
            mCube.draw(gl);
               
            gl.glLoadIdentity();                                    
                
            //mCubeRotation -= 0.15f;
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
            gl.glViewport(0, 0, width, height);

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
        }

}

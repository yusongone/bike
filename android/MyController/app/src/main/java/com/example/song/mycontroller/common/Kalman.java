package com.example.song.mycontroller.common;

import android.util.Log;

/**
 * Created by song on 15/1/8.
 */
public class Kalman {
    float C_0=1.0f;
    float Q_angle=0.001f;
    float Q_gyro=0.003f;
    float R_angle=0.5f;
    float q_bias=0.0f;
    float angle_err=0.0f;
    float PCt_0=0.0f;
    float PCt_1=0.0f;
    float E=0.0f;
    float K_0=0.0f;
    float K_1=0.0f;
    float t_0=0.0f;
    float t_1=0.0f;
    float angle=0.0f;
    float angle_dot=0.0f;
    float[][] P={{1.0f,0.0f},{0.0f,1.0f}};
    float[] Pdot={0.0f,0.0f,0.0f,0.0f};

    void Kalman(){

    };

    public float getAngle(float angle_m, float gyro_m, float dt){
       angle+=(gyro_m-q_bias) * dt;
       angle_err = angle_m - angle;
       Pdot[0] = Q_angle - P[0][1] - P[1][0];
       Pdot[1] = -P[1][1];
       Pdot[2] = -P[1][1];
       Pdot[3] = Q_gyro;
       P[0][0] += Pdot[0] * dt;
       P[0][1] += Pdot[1] * dt;
       P[1][0] += Pdot[2] * dt;
       P[1][1] += Pdot[3] * dt;
       PCt_0 = C_0 * P[0][0];
       PCt_1 = C_0 * P[1][0];
       E = R_angle + C_0 * PCt_0;
       K_0 = PCt_0 / E;
       K_1 = PCt_1 / E;
       t_0 = PCt_0;
       t_1 = C_0 * P[0][1];
       P[0][0] -= K_0 * t_0;
       P[0][1] -= K_0 * t_1;
       P[1][0] -= K_1 * t_0;
       P[1][1] -= K_1 * t_1;
       angle += K_0 * angle_err;
       q_bias += K_1 * angle_err;
       angle_dot = gyro_m-q_bias;
       return angle;
    }
    public float getAngleDot(){
        return angle_dot;
    }
}

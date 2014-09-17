/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Ivan1pl
 */
public class PlotModel {
    @Getter
    private final String name;
    private ItemStack[][][] model;
    @Getter
    private int sizex;
    private int sizey;
    @Getter
    private int sizez;
    private Location point1 = null;
    private Location point2 = null;
    public PlotModel(String modelname){
        name = modelname;
        sizex = sizey = sizez = 0;
    }
    public PlotModel(String modelname, File in){
        name = modelname;
        try {
            Scanner stream = new Scanner(in);
            sizex = Integer.parseInt(stream.nextLine());
            sizey = Integer.parseInt(stream.nextLine());
            sizez = Integer.parseInt(stream.nextLine());
            if(sizex == 0 || sizey == 0 || sizez == 0){
                model = null;
            }
            else{
                model = new ItemStack[sizex][sizey][sizez];
            }
            for(int x = 0; x < sizex; ++x){
                for(int y = 0; y < sizey; ++y){
                    for(int z = 0; z < sizez; ++z){
                        Material mat = Material.getMaterial(stream.nextLine());
                        model[x][y][z] = new ItemStack(mat,1,Short.parseShort(stream.nextLine()));
                    }
                }
            }
            stream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setPoint1(Location l){
        point1 = new Location(l.getWorld(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
    }
    public void setPoint2(Location l){
        point2 = new Location(l.getWorld(),l.getBlockX(),l.getBlockY(),l.getBlockZ());
    }
    public void saveModel(File out, Player p){
        if(point1 != null && point2 != null && point1.getWorld().getName().equals(point2.getWorld().getName())){
            sizex = Math.abs(point1.getBlockX()-point2.getBlockX())+1;
            sizey = Math.abs(point1.getBlockY()-point2.getBlockY())+1;
            sizez = Math.abs(point1.getBlockZ()-point2.getBlockZ())+1;
            model = new ItemStack[sizex][sizey][sizez];
            Location corner = new Location(point1.getWorld(), Math.min(point1.getBlockX(), point2.getBlockX()),
                                                              Math.min(point1.getBlockY(), point2.getBlockY()),
                                                              Math.min(point1.getBlockZ(), point2.getBlockZ()));
            Location iter;
            if(p != null){
                p.sendMessage(Freebuild.prefix + "Getting blocks...");
            }
            for(int x = 0; x < sizex; ++x){
                for(int y = 0; y < sizey; ++y){
                    for(int z = 0; z < sizez; ++z){
                        iter = new Location(corner.getWorld(),corner.getBlockX()+x,corner.getBlockY()+y,corner.getBlockZ()+z);
                        model[x][y][z] = iter.getBlock().getState().getData().toItemStack();
                    }
                }
            }
            if(p != null){
                p.sendMessage(Freebuild.prefix + "Done");
            }
            if(p != null){
                p.sendMessage(Freebuild.prefix + "Saving model...");
            }
            try {
                FileWriter fos = new FileWriter(out);
                PrintWriter stream = new PrintWriter(fos);
                stream.println(sizex);
                stream.println(sizey);
                stream.println(sizez);
                for(int x = 0; x < sizex; ++x){
                    for(int y = 0; y < sizey; ++y){
                        for(int z = 0; z < sizez; ++z){
                            stream.println(model[x][y][z].getType().toString());
                            stream.println(model[x][y][z].getDurability());
                        }
                    }
                    if(p != null){
                        int pc = ((x+1)*sizey*sizez*100)/(sizex*sizey*sizez);
                        p.sendMessage(Freebuild.prefix + "Saving model... " + Integer.toString(pc) + "%");
                    }
                }
                if(p != null){
                    p.sendMessage(Freebuild.prefix + "Done");
                }
                stream.close();
                fos.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            if(p != null){
                p.sendMessage(Freebuild.prefix + "You have to set both points");
            }
        }
    }
    public void generate(Location l){
        Location corner = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
        Location iter;
        for(int x = 0; x < sizex; ++x){
            for(int y = 0; y < sizey; ++y){
                for(int z = 0; z < sizez; ++z){
                    iter = new Location(corner.getWorld(),corner.getBlockX()+x,corner.getBlockY()+y,corner.getBlockZ()+z);
                    iter.getBlock().setType(model[x][y][z].getType());
                    BlockState state = iter.getBlock().getState();
                    state.setData(model[x][y][z].getData());
                    state.update(true);
                }
            }
        }
    }
    public static void generateDefaultModel(File out){
        try {
            FileWriter fos = new FileWriter(out);
            PrintWriter stream = new PrintWriter(fos);
            stream.println(48);
            stream.println(0);
            stream.println(48);
            stream.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlotModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*
 *    This file is part of LGDEditTool (LGDET).
 *
 *    LGDET is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    any later version.
 *
 *    LGDET is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with LGDET.  If not, see <http://www.gnu.org/licenses/>.
 */

package LGDEditTool.Templates;

import LGDEditTool.db.DatabaseBremen;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
/**
 *
 * @author Alexander Richter
 */
public class TemplatesOntology {
    
    static public String ontologie(String tag){
        String s=new String("");
        
        s +="<br>"+tag;
        s +="<br>drüber:\n";
        try{
        DatabaseBremen database = new DatabaseBremen();
	database.connect();
	Object[][] a = database.execute("SELECT k FROM lgd_map_resource_kv WHERE v='"+tag+"'");
        for(int i=0;i<a.length;i++){s+=""+a[i][0].toString()+"\n";}
        }catch(Exception e){}
        
        s +="<br>drunter:\n";
        try{
        DatabaseBremen database = new DatabaseBremen();
	database.connect();
	Object[][] a = database.execute("SELECT v FROM lgd_map_resource_kv WHERE k='"+tag+"'");
        for(int i=0;i<a.length;i++){s+=""+a[i][0].toString()+"\n";}
        }catch(Exception e){}
      
        s+= "<canvas id=\"canvas\" width='100' height='100'>";
        s+= "<%var canvas=document.getElementById(\"canvas\");if(!canvas.getContext){return;}var ctx=canvas.getContext(\"2d\");ctx.fillStyle=\"rgb(200,0,0)\";ctx.fillRect(10,10,55,50);ctx.fillStyle=\"rgba(0,0,200,0.5)\";ctx.fillRect(30,30,55,50);%>";
        
        return s;
    }
    
   
    
}

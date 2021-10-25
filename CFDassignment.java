import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import org.jfree.ui.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

class CFDassignment
{
  static String title, directory = "D:\\Dropbox\\Documents\\IIT Kharagpur\\Academics\\Aerospace Engineering\\Semester 5\\Computational Fluid Dynamics\\Assignment";
  static double udata_CD2[][],udata_UD1[][];
  int x, y;
  double PI=3.141590, dt, dx, c=1, CFL;
  XYSeriesCollection data[]=new XYSeriesCollection[2];

  public CFDassignment(final String title1)throws IOException
  {
    int ch;
    BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
    getValues();
    setBoundary();
    calculate();
    store(title);
    System.out.println("dx = " + dx + " dt = " + dt + " CFL = " + CFL);
    System.out.println("Do you want to display a graph?(1/0)");
    ch=Integer.parseInt(in.readLine());
    if(ch==1)
    printCD2(title1);
  }

  public void printCD2(String title1)throws IOException
  {
    JFrame f=new JFrame(title1);
    int i,j;
    Boolean cs;
    data[0] = new XYSeriesCollection();
    XYSeries series[]= new XYSeries[y];
    for(i=0;i<y;i+=(y/4))
    {
      series[i]=new XYSeries("Wave at t = " + (i*dt) + "\t");
      for(j=0;j<=x;j++)
      {
        series[i].add(j*dx, udata_CD2[i][j]);
      }
      data[0].addSeries(series[i]);
    }
    final JFreeChart chart = ChartFactory.createXYLineChart("CD2_"+title,"Phase","Amplitude",data[0],PlotOrientation.VERTICAL,true,true,false);
    final ChartPanel chartPanel = new ChartPanel(chart);
    ChartUtilities.saveChartAsPNG(new File(directory + "\\Graphs\\CD2_" + title + ".png"), chart, 800, 570);
    chartPanel.setPreferredSize(new java.awt.Dimension(800, 570));
    f.setContentPane(chartPanel);
    f.pack();
    RefineryUtilities.centerFrameOnScreen(f);
    f.addWindowListener(new WindowAdapter(){ @Override public void windowClosing(WindowEvent e){try{printUD1(title1);}catch(IOException exp){System.out.println("Error.");}}});
    f.setVisible(true);
  }

  public void printUD1(String title1)throws IOException
  {
    JFrame f=new JFrame(title1);
    int i,j;
    Boolean cs;
    data[1] = new XYSeriesCollection();
    XYSeries series[]= new XYSeries[y];
    for(i=0;i<y;i+=(y/4))
    {
      series[i]=new XYSeries("Wave at t = " + (i*dt) + "\t");
      for(j=0;j<=x;j++)
      {
        series[i].add(j*dx, udata_UD1[i][j]);
      }
      data[1].addSeries(series[i]);
    }
    final JFreeChart chart = ChartFactory.createXYLineChart("UD1_"+title,"Phase","Amplitude",data[1],PlotOrientation.VERTICAL,true,true,false);
    final ChartPanel chartPanel = new ChartPanel(chart);
    ChartUtilities.saveChartAsPNG(new File(directory + "\\Graphs\\UD1_" + title + ".png"), chart, 800, 570);
    chartPanel.setPreferredSize(new java.awt.Dimension(800, 570));
    f.setContentPane(chartPanel);
    f.pack();
    RefineryUtilities.centerFrameOnScreen(f);
    f.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    f.setVisible(true);
  }

  public void getValues()throws IOException
  {
    BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Specify the number of subdomains: ");
    x=Integer.parseInt(in.readLine());
    System.out.print("Specify the number of iterations: ");
    y=Integer.parseInt(in.readLine());
    System.out.print("Specify the Courant Number (0-1 for stable solution): ");
    CFL=Double.parseDouble(in.readLine());
    udata_CD2=new double[y][x+1];
    udata_UD1=new double[y][x+1];
    if(x<=0)
    {
      System.out.print("Incorrect input for subdomains, using the default value, 200");
      x=200;
    }
    if(y<=0)
    {
      System.out.print("Incorrect input for iterations, using the default value, 50");
      y=50;
    }
    if(CFL<=0)
    {
      System.out.print("Incorrect input for CFL, using the default value, 0.05");
      CFL=0.05;
    }
    title=Integer.valueOf(x).toString() + "_" + Integer.valueOf(y).toString() + "_" + Double.valueOf(CFL).toString();
    dx=2*PI/x;
    dt=dx*CFL/c;
  }

  public static void main(String args[])throws IOException
  {
    CFDassignment obj=new CFDassignment("Graph");
  }

  public void setBoundary()
  {
    int i, j;
    for (i=0;i<=x;i++)
    {
      udata_CD2[0][i]=Math.sin(i*dx);
      udata_UD1[0][i]=Math.sin(i*dx);
    }
    for(j=1;j<y;j++)
    {
      udata_CD2[j][0]=-Math.sin(j*dt);
      udata_CD2[j][x]=Math.sin(2*PI-j*dt);
      udata_UD1[j][0]=-Math.sin(j*dt);
    }
  }

  public void calculate()
  {
    int i, j=1;
    for(i=1;i<y;i++)
    {
      for(j=1;j<x;j++)
      {
        udata_CD2[i][j]=udata_CD2[i-1][j] + (dt*c)/(2*dx)*(udata_CD2[i-1][j-1]-udata_CD2[i-1][j+1]);
        udata_UD1[i][j]=udata_UD1[i-1][j] + (dt*c)/(dx)*(udata_UD1[i-1][j-1]-udata_UD1[i-1][j]);
      }
      udata_UD1[i][j]=udata_UD1[i-1][j] + (dt*c)/(dx)*(udata_UD1[i-1][j-1]-udata_UD1[i-1][j]);
    }
  }

  public void store(String title)throws IOException
  { int i, j;
    double a;
    PrintWriter out_CD2 = new PrintWriter(directory + "\\Data Sheets\\CD2_" + title + ".csv");
    PrintWriter out_UD1 = new PrintWriter(directory + "\\Data Sheets\\UD1_" + title + ".csv");
    out_CD2.write("dt\\dx, ");
    out_UD1.write("dt\\dx, ");
    for(j=0;j<=x;j++)
    {
      out_CD2.write(Double.valueOf(j*dx).toString() + ", ");
      out_UD1.write(Double.valueOf(j*dx).toString() + ", ");
    }
    for(i=0;i<y;i++)
    {
      out_CD2.write("\n" + Double.valueOf(i*dt).toString() + ", ");
      out_UD1.write("\n" + Double.valueOf(i*dt).toString() + ", ");
      for(j=0;j<=x;j++)
      {
        out_CD2.write(Double.valueOf(udata_CD2[i][j]).toString() + ", ");
        out_UD1.write(Double.valueOf(udata_UD1[i][j]).toString() + ", ");
      }
    }
    out_CD2.close();
    out_UD1.close();
  }
}

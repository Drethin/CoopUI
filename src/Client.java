import javax.swing.*;
import java.awt.*;

/**
 * Created by Alex on 26/07/2016.
 */
public class Client {
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientView view = new ClientView();
                view.setSize(800, 600);
                view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                view.setLocationRelativeTo(null);
                view.setVisible(true);

            }
        });
    }
}

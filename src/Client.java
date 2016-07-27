import javax.swing.*;
import java.awt.*;

/**
 * Created by Alex on 26/07/2016.
 * TEST
 */
class Client {
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {

            e.printStackTrace();
        }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientView view = new ClientView();
                view.setSize(800, 600);
                view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                view.setLocationRelativeTo(null);
                view.setVisible(true);

            }
        });
    }
}

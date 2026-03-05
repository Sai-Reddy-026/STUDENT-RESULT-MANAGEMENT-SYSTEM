import javax.lang.model.type.NullType;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class addteacherpage extends JFrame{
    public addteacherpage()  {
        setTitle("Add Teacher");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JButton exit=new JButton("Back");
        exit.setBounds(0,0,75,25);
        exit.addActionListener(e->
        {
            dispose();
            new AdminFrame();
        });
        add(exit);

        JLabel tag = new JLabel("Teacher's Details");
        tag.setBounds(150, 20, 100, 25);
        add(tag);
        JLabel teacherid = new JLabel("ID");
        teacherid.setBounds(10, 40, 100, 25);
        add(teacherid);

        JLabel nameboxlabel = new JLabel("Name");
        nameboxlabel.setBounds(10, 70, 100, 25);
        add(nameboxlabel);

        JLabel DeptLabel = new JLabel("Department");
        DeptLabel.setBounds(10, 100, 100, 25);
        add(DeptLabel);

        JLabel Emaillabel = new JLabel("Email ID");
        Emaillabel.setBounds(10, 130, 100, 25);
        add(Emaillabel);

        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(10,170,100,25);
        add(passLabel);

        JTextField tidbox = new JTextField();
        tidbox.setBounds(150, 40, 200, 25);
        add(tidbox);

        JTextField namebox = new JTextField();
        namebox.setBounds(150, 70, 200, 25);
        add(namebox);

        JTextField Deptbox = new JTextField();
        Deptbox.setBounds(150, 100, 200, 25);
        add(Deptbox);

        JTextField Emailbox = new JTextField();
        Emailbox.setBounds(150, 130, 200, 25);
        add(Emailbox);

        JTextField Passbox = new JTextField();
        Passbox.setBounds(150,160,200,25);
        add(Passbox);

        JButton Add = new JButton("ADD");
        Add.setBounds(200, 190, 100, 25);
        Add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=tidbox.getText().trim();
                String name=namebox.getText();
                String Email=Emailbox.getText();
                String Dept=Deptbox.getText();
                String pass=Passbox.getText();
                try{

                    Connection con = DBConnection.getDbConnection();
                    System.out.println("Connected Successfully");
                    String sql="Insert INtO teacher VALUES(?,?,?,?,?)";
                    PreparedStatement pt=con.prepareStatement(sql);
                    pt.setString(1,id);
                    pt.setString(2,name);
                    pt.setString(3,Email);
                    pt.setString(4,Dept);
                    pt.setString(5,pass);
                    pt.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Data inserted Successfully","Success",JOptionPane.INFORMATION_MESSAGE);
                }
                catch(Exception error)
                {
                    JOptionPane.showMessageDialog(null,error,"Failed",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        add(Add);
    }
}

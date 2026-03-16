import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class addstudentpage extends JFrame {
    addstudentpage()
    {
        setTitle("Add Students");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        setSize(600,300);

        JLabel banner=new JLabel("Student Details");
        banner.setBounds(150,20,200,25);
        add(banner);

        JLabel Idlabel=new JLabel("Id");
        Idlabel.setBounds(40,40,150,25);
        add(Idlabel);

        JTextField Idbox=new JTextField();
        Idbox.setBounds(200,40,150,25);
        add(Idbox);

        JLabel Namelabel=new JLabel("Name");
        Namelabel.setBounds(40,70,150,25);
        add(Namelabel);

        JTextField Namebox=new JTextField();
        Namebox.setBounds(200,70,150,25);
        add(Namebox);

        JLabel departmentlabel=new JLabel("Department");
        departmentlabel.setBounds(40,100,150,25);
        add(departmentlabel);

        JTextField departmentbox = new JTextField();
        departmentbox.setBounds(200,100,150,25);
        add(departmentbox);

        JLabel emaillabel = new JLabel("Email");
        emaillabel.setBounds(40,130,150,25);
        add(emaillabel);

        JTextField emailbox = new JTextField();
        emailbox.setBounds(200,130,150,25);
        add(emailbox);

        JLabel Passwordlabel=new JLabel("Password");
        Passwordlabel.setBounds(40,160,150,25);
        add(Passwordlabel);

        JTextField Passwordbox = new JTextField();
        Passwordbox.setBounds(200,160,150,25);
        add(Passwordbox);

        JButton exit= new JButton("Back");
        exit.setBounds(10,200,100,25);
        exit.addActionListener(e->{
            dispose();
            new AdminFrame();
        });
        add(exit);

        JButton create=new JButton("Create");
        create.setBounds(225,200,100,25);
        add(create);
        create.addActionListener(e->{
            String id=Idbox.getText().trim();
            String name=Namebox.getText().trim();
            String dept=departmentbox.getText().trim();
            String email=emailbox.getText().trim();
            String Password=Passwordbox.getText().trim();
            try{
                String dbURL="jdbc:mysql://localhost:3306/result_management";
                String dbUser="root";
                String dbPassword="root";
                Connection con = DriverManager.getConnection(dbURL,dbUser,dbPassword);
                System.out.println("Connected Successfully");
                String sql="Insert INtO student VALUES(?,?,?,?,?)";
                PreparedStatement pt=con.prepareStatement(sql);
                pt.setString(1,id);
                pt.setString(2,name);
                pt.setString(3,email);
                pt.setString(4,Password);
                pt.setString(5,dept);
                pt.executeUpdate();
                JOptionPane.showMessageDialog(null,"Data inserted Successfully","Success",JOptionPane.INFORMATION_MESSAGE);
            }
            catch (Exception error)
            {
                System.out.print(error);
            }
        });

    }
}

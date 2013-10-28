package frontened;

import java.awt.BorderLayout;

import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import backend.ExcelSheetReader;
import backend.Simulacion;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.TextField;
import javax.swing.JFormattedTextField;
import javax.swing.JEditorPane;
import javax.swing.Box;
import javax.swing.JToggleButton;
import javax.swing.JTextArea;
import java.awt.Color;

public class VentanaPrincipal extends JFrame {

	static Simulacion simulacion;

	/**
	 * 
	 * Launch the application.
	 * 
	 */
	JTextArea textArea = new JTextArea();
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal frame = new VentanaPrincipal();
					frame.setVisible(true);
					
					
					
					
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaPrincipal() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaPrincipal.class.getResource("/frontened/Citroen Logo 3.jpg")));
		setTitle("Agendador Citroen ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 316, 121);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);
		
		JMenuItem mntmCargarBd = new JMenuItem("Cargar BD");
		mntmCargarBd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				simulacion = new Simulacion(5,5,5);
				simulacion.correr();
				
				textArea.setText("Resultados listos en su carpeta");
				
			}
		});
		mnArchivo.add(mntmCargarBd);
		
		JMenu mnAyuda = new JMenu("Ayuda");
		menuBar.add(mnAyuda);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		
		
		
		panel.add(textArea);
		textArea.setText("Cargue su Base de Datos");
		textArea.setEditable(false);
		textArea.setBackground((Color) null);
	}

}

package es.ucm.fdi.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import es.ucm.fdi.control.*;
import es.ucm.fdi.extra.dialog.DialogWindow;
import es.ucm.fdi.ini.*;

@SuppressWarnings("serial")
/**
 * Panel que contiene un TextArea en su interior.Responsabilidades:
 * Permite devolver un OutputStream que escriba en el interior del JTextArea.
 * Crea un editor si es el área es modificable para poder añadir plantillas y realizar acciones.
 * Si no es modificable crea un editor para poder filtrar los objetos de simulación a través de el
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class GUITextArea extends JPanel {
	private JTextArea area;
	private OutputStream out;
	private static final String iniResourceName="Events.ini";
	public GUITextArea(boolean modifiable, String title,JTextArea area,SimulatorAction... actions)throws IOException {
		super(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(title));

		this.area = area;
		this.area.setEditable(modifiable);
		add(new JScrollPane(this.area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
			addEditor(modifiable,actions);
		out=new PrintStream(new OutputStream(){
			@Override
			public void write(int b) throws IOException {
				area.insert( String.valueOf( ( char )b ),area.getCaretPosition() );
			}});
	}
	public void setText(String text){
		area.setText(text);
	}
	public String getText(){
		return area.getText();
	}
	public void clear() {
		area.setText("");
	}

	public OutputStream getGUITextAreaOutputStream(){
		return out;
	}

	public InputStream getFileTextInputStream() {
		return new ByteArrayInputStream(area.getText().getBytes());
	}

	public void readFile(File file) throws Exception{
		String s = "";
		s = new Scanner(file).useDelimiter("\\A").next();

		area.setText(s);
	}

	private void addEditor(boolean modifiable, SimulatorAction... actions) throws IOException{
		JPopupMenu editorPopupMenu = new JPopupMenu();
		if(modifiable){
			int j=0;
			JMenu subMenu = new JMenu("Add Template");
			String[] options = { "New RR Junction", "New MC Junction",
					"New Junction", "New Dirt Road", "New Lanes Road", "New Road",
					"New Bike", "New Car", "New Vehicle", "Make Vehicle Faulty" };
			try {
				Ini init=new Ini();
				init.load(getClass().getClassLoader().getResourceAsStream(iniResourceName));
				List<IniSection> list = init.getSections();
			
				for(IniSection i:list){
					JMenuItem menuItem=new JMenuItem(options[j]);
					menuItem.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							area.insert(i.toString(),area.getCaretPosition());
							actions[0].setEnabled(true);
						}
					});
					subMenu.add(menuItem);
					j++;
				}
			} catch (IOException e) {
			throw new IOException("Failure reading the auxiliar template ini:\n"+iniResourceName, e);
			}
	
		
			editorPopupMenu.add(subMenu);
			editorPopupMenu.addSeparator();
			editorPopupMenu.add(actions[0]);
			editorPopupMenu.add(actions[1]);
			editorPopupMenu.add(actions[2]);
		}else{
			editorPopupMenu.add(actions[3]);
		}
		area.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger() && editorPopupMenu.isEnabled()) {
					editorPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}
}

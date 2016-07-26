import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Alex on 26/07/2016.
 */
public class ClientModel {

    public final static int NO_ERR = 0;
    public final static int CFG_NOT_FOUND = -1;
    public final static int INVALID_CFG = -2;
    public final static int CLIENT_EXISTS = -3;
    public final static int POLICY_EXISTS = -4;
    public final static int CLIENT_NOT_EXISTS = -5;
    public final static int RENAME_FAIL = -6;
    public final static int ERR_NOT_FOUND = 404;
    public final static int CLIENT = 0;
    public final static int BUSI = 1;
    public final static int FARM = 2;
    public final static int HAB = 3;
    public final static int LIFE = 4;
    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    Document doc;
    NodeList fsNList;
    NodeList clientFS;
    NodeList habFS;
    NodeList busiFS;
    NodeList farmFS;
    NodeList lifeFS;
    NodeList empNList;
    int habToggle;
    int farmToggle;
    int busiToggle;
    int lifeToggle;
    String name;
    String employee;
    ArrayList<String> employees;
    String parentDirectory;
    ArrayList<Object[]> data;
    Object[] cData;
    private int errMsg;

    public ClientModel() {
        File configFile = new File("config.xml");
        if (!configFile.exists()) {
            errMsg = CFG_NOT_FOUND;
            return;
        }
        String tmpDirectory = new File("").getAbsolutePath();
        parentDirectory = tmpDirectory.substring(0,
                tmpDirectory.lastIndexOf("\\") + 1); // Getting parent directory
        // of .jar
        factory = DocumentBuilderFactory.newInstance();
        try { // Reading config file
            builder = factory.newDocumentBuilder();
            doc = builder.parse(configFile);
        } catch (ParserConfigurationException e) {
            errMsg = INVALID_CFG;
            return;
        } catch (SAXException e) {
            errMsg = INVALID_CFG;
            return;
        } catch (IOException e) {
            errMsg = INVALID_CFG;
            return;
        }

        doc.getDocumentElement().normalize();
        fsNList = doc.getElementsByTagName("fileStructure").item(0)
                .getChildNodes();
        empNList = doc.getElementsByTagName("employees").item(0)
                .getChildNodes();
        employees = new ArrayList<String>();
        for (int i = 0; i < empNList.getLength(); i++) { // Creating list of
            // employees
            if (!empNList.item(i).getTextContent().trim().isEmpty()) {
                employees.add(empNList.item(i).getTextContent());
            }
        }

        for (int i = 0; i < fsNList.getLength(); i++) {
            Node tmpNode = fsNList.item(i);
            if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) tmpNode).getAttribute("name").equals(
                        "CLIENT FILES")) {
                    clientFS = tmpNode.getChildNodes();
                }
                if (((Element) tmpNode).getAttribute("name").equals(
                        "COMMERCIAL FILES")) {
                    busiFS = tmpNode.getChildNodes();
                }
                if (((Element) tmpNode).getAttribute("name").equals(
                        "FARM FILES")) {
                    farmFS = tmpNode.getChildNodes();
                }
                if (((Element) tmpNode).getAttribute("name").equals(
                        "HABITATIONAL FILES")) {
                    habFS = tmpNode.getChildNodes();
                }
                if (((Element) tmpNode).getAttribute("name").equals(
                        "LIFE FILES")) {
                    lifeFS = tmpNode.getChildNodes();
                }
            }
        }
        data = new ArrayList<Object[]>();
        habToggle = -1;
        farmToggle = -1;
        busiToggle = -1;
        lifeToggle = -1;
        employee = "";
        name = "";
        errMsg = 0;
    }

    public static void clean(Node node) {
        NodeList childNodes = node.getChildNodes();

        for (int n = childNodes.getLength() - 1; n >= 0; n--) {
            Node child = childNodes.item(n);
            short nodeType = child.getNodeType();

            if (nodeType == Node.ELEMENT_NODE)
                clean(child);
            else if (nodeType == Node.TEXT_NODE) {
                String trimmedNodeVal = child.getNodeValue().trim();
                if (trimmedNodeVal.length() == 0)
                    node.removeChild(child);
                else
                    child.setNodeValue(trimmedNodeVal);
            } else if (nodeType == Node.COMMENT_NODE)
                node.removeChild(child);
        }
    }

    public int addEmployee(String employee) {
        File configFile = new File("config.xml");
        if (!configFile.exists()) {
            return CFG_NOT_FOUND;
        }
        factory = DocumentBuilderFactory.newInstance();
        try { // Reading config file
            builder = factory.newDocumentBuilder();
            doc = builder.parse(configFile);
        } catch (ParserConfigurationException e) {
            return INVALID_CFG;
        } catch (SAXException e) {
            return INVALID_CFG;
        } catch (IOException e) {
            return INVALID_CFG;
        }
        doc.getDocumentElement().normalize();
        NodeList empNode = doc.getElementsByTagName("employees");
        Text empName = doc.createTextNode(employee);
        Element e = doc.createElement("employee");
        e.appendChild(empName);
        empNode.item(0).insertBefore(e, empNode.item(1));
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult("config.xml");
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            employees.add(employee);
            return NO_ERR;
        } catch (TransformerConfigurationException e2) {
            return ERR_NOT_FOUND;
        } catch (TransformerFactoryConfigurationError e2) {
            return ERR_NOT_FOUND;
        } catch (TransformerException e1) {
            return ERR_NOT_FOUND;
        }
    }

    public int removeEmployee(String employee) {

        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).equals(employee)) employees.remove(i);
        }
        //return NO_ERR;
        File configFile = new File("config.xml");
        if (!configFile.exists()) {
            return CFG_NOT_FOUND;
        }
        factory = DocumentBuilderFactory.newInstance();
        try { // Reading config file
            builder = factory.newDocumentBuilder();
            doc = builder.parse(configFile);
        } catch (ParserConfigurationException e) {
            return INVALID_CFG;
        } catch (SAXException e) {
            return INVALID_CFG;
        } catch (IOException e) {
            return INVALID_CFG;
        }
        doc.getDocumentElement().normalize();
        NodeList empNode = doc.getElementsByTagName("employees");
        for (int i = 0; i < empNode.item(0).getChildNodes().getLength(); i++) {
            Node tmpNode = empNode.item(0).getChildNodes().item(i);
            if (tmpNode.getTextContent().equals(employee)) {
                empNode.item(0).removeChild(empNode.item(0).getChildNodes().item(i));
            }
        }
        clean(doc.getFirstChild());
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult("config.xml");
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            return NO_ERR;
        } catch (TransformerConfigurationException e2) {
            return ERR_NOT_FOUND;
        } catch (TransformerFactoryConfigurationError e2) {
            return ERR_NOT_FOUND;
        } catch (TransformerException e1) {
            return ERR_NOT_FOUND;
        }
    }

    public void resetNewClient() {
        employee = "";
        habToggle = -1;
        farmToggle = -1;
        busiToggle = -1;
        lifeToggle = -1;
        name = "";
        cData = null;
    }

    public void setEmployee(String name) {
        employee = name;
    }

    public void toggleHab() {
        habToggle *= -1;
    }

    public void toggleFarm() {
        farmToggle *= -1;
    }

    public void toggleBusi() {
        busiToggle *= -1;
    }

    public void toggleLife() {
        lifeToggle *= -1;
    }

    /*
     * public int setName(String name) { this.name = name; return 1; }
     */
    public void setCData(Object[] c) {
        cData = c;
    }

    public ArrayList<Object[]> getData() {
        return data;
    }

    public int getErr() {
        return errMsg;
    }

    public String createURL(Object[] row, int type) {
        String folderName = "";
        String empl = null;
        switch (type) {
            case CLIENT:
                folderName = "CLIENT FILE\\";
                empl = (String) row[2];
                break;
            case BUSI:
                folderName = "COMMERCIAL FILE\\";
                empl = null;
                break;
            case FARM:
                folderName = "FARM FILE\\";
                empl = null;
                break;
            case HAB:
                folderName = "HABITATIONAL FILE\\";
                empl = null;
                break;
            case LIFE:
                folderName = "LIFE FILE\\";
                empl = null;
                break;
        }
        String lName = (String) row[0];
        String fName = (String) row[1];

        String character = lName.substring(0, 1);
        if (character.matches("[0-9]")) {
            character = "1";
        }
        if (empl == null || empl.isEmpty()) {
            if (fName == null || fName.isEmpty()) {
                return (parentDirectory + folderName + character + "\\" + lName);
            } else {
                return (parentDirectory + folderName + character + "\\" + lName
                        + ", " + fName);
            }
        } else if (fName == null || fName.isEmpty()) {
            return (parentDirectory + folderName + character + "\\" + lName
                    + "_" + empl);
        } else {
            return (parentDirectory + folderName + character + "\\" + lName
                    + ", " + fName + "_" + empl);
        }
    }

    public int fetchClientData() { // Getting client list to populate data array
        int numClients;
        String fName;
        String lName;
        String employee;
        String command = "powershell (ls '../CLIENT FILE' -directory | ls -name -directory | measure).Count";

        try {
            Process psProcess = Runtime.getRuntime().exec(command); // Getting
            // number of
            // clients
            psProcess.getOutputStream().close();
            String line;
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    psProcess.getInputStream()));
            line = stdout.readLine();
            numClients = Integer.parseInt(line);
            stdout.close();
            data = new ArrayList<Object[]>(numClients);

            command = "powershell.exe ls '../CLIENT FILE' -directory | ls -name -directory";
            psProcess = Runtime.getRuntime().exec(command); // Getting client
            // list
            psProcess.getOutputStream().close();
            stdout = new BufferedReader(new InputStreamReader(
                    psProcess.getInputStream()));
            for (int i = 0; i < numClients; i++) {
                line = stdout.readLine();
                if (!line.contains("_")) {
                    employee = null;
                } else {
                    employee = (line.substring(line.lastIndexOf("_") + 1));
                }
                if (!line.contains(",")) {
                    fName = null;
                    if (employee != null)
                        lName = line.substring(0, line.lastIndexOf("_"));
                    else
                        lName = line;
                } else {
                    lName = line.substring(0, line.indexOf(","));
                    if (employee != null)
                        fName = line.substring(line.indexOf(",") + 2,
                                line.lastIndexOf("_"));
                    else
                        fName = line.substring(line.indexOf(",") + 2);
                }
                Object[] tmpData = new Object[3];
                tmpData[0] = lName;
                tmpData[1] = fName;
                tmpData[2] = employee;
                data.add(i, tmpData);
            }
            stdout.close();
        } catch (NumberFormatException e) {
            return ERR_NOT_FOUND;
        } catch (IOException e) {
            return ERR_NOT_FOUND;
        }
        return NO_ERR;
    }

    public int createClient() {
        File newClient;

        for (int i = 0; i < employees.size(); i++) { // Checking if any client
            // exists
            newClient = new File(createURL(new Object[]{cData[0], cData[1],
                    employees.get(i)}, CLIENT));
            if (newClient.exists())
                return CLIENT_EXISTS;
        }
        newClient = new File(createURL(
                new Object[]{cData[0], cData[1], null}, CLIENT));
        if (newClient.exists())
            return CLIENT_EXISTS;
        newClient = new File(createURL(new Object[]{cData[0], cData[1],
                "Dead"}, CLIENT));
        if (newClient.exists())
            return CLIENT_EXISTS;
        newClient = new File(createURL(cData, CLIENT));
        if (newClient.exists())
            return CLIENT_EXISTS;

        newClient.mkdirs();
        for (int i = 0; i < clientFS.getLength(); i++) { // Building client
            // directory
            Node nNode = clientFS.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                File tmp = new File(newClient, nNode.getTextContent());
                tmp.mkdirs();
            }
        }
        if (habToggle == 1) {
            if ((errMsg = addHab(newClient)) != 0) {
                return errMsg;
            }
        }
        if (farmToggle == 1) {
            if ((errMsg = addFarm(newClient)) != 0) {
                return errMsg;
            }
        }
        if (busiToggle == 1) {
            if ((errMsg = addBusi(newClient)) != 0) {
                return errMsg;
            }
        }
        if (lifeToggle == 1) {
            if ((errMsg = addLife(newClient)) != 0) {
                return errMsg;
            }
        }
        return NO_ERR;
    }

    public int add() {
        File newClient = new File(createURL(cData, CLIENT));
        if (!newClient.exists())
            return CLIENT_NOT_EXISTS;
        if (habToggle == 1) {
            if ((errMsg = addHab(newClient)) != 0) {

                return errMsg;
            }
        }
        if (farmToggle == 1) {
            if ((errMsg = addFarm(newClient)) != 0) {
                return errMsg;
            }
        }
        if (busiToggle == 1) {
            if ((errMsg = addBusi(newClient)) != 0) {
                return errMsg;
            }
        }
        if (lifeToggle == 1) {
            if ((errMsg = addLife(newClient)) != 0) {
                return errMsg;
            }
        }
        return NO_ERR;
    }

    public int changeEmp(String name) {
        File editClient = new File(createURL(cData, CLIENT));
        if (editClient.renameTo(new File(createURL(new Object[]{cData[0],
                cData[1], name}, CLIENT))))
            return 0;
        return RENAME_FAIL;
    }

    public int addHab(File newClient) {
        File newHab = new File(createURL(cData, HAB));
        if (newHab.exists())
            return POLICY_EXISTS;
        newHab.mkdirs();
        for (int i = 0; i < habFS.getLength(); i++) {
            Node nNode = habFS.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                File tmp = new File(newHab, nNode.getTextContent());
                tmp.mkdirs();
            }
        }
        FileUtils.createShortcut(newHab, new File(newClient, "HABITATIONAL"));
        return NO_ERR;
    }

    public int addFarm(File newClient) {
        File newFarm = new File(createURL(cData, FARM));
        if (newFarm.exists())
            return POLICY_EXISTS;
        newFarm.mkdirs();
        for (int i = 0; i < farmFS.getLength(); i++) {
            Node nNode = farmFS.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                File tmp = new File(newFarm, nNode.getTextContent());
                tmp.mkdirs();
            }
        }
        FileUtils.createShortcut(newFarm, new File(newClient, "FARM"));
        return NO_ERR;
    }

    public int addBusi(File newClient) {
        File newBusi = new File(createURL(cData, BUSI));
        if (newBusi.exists())
            return POLICY_EXISTS;
        newBusi.mkdirs();
        for (int i = 0; i < busiFS.getLength(); i++) {
            Node nNode = busiFS.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                File tmp = new File(newBusi, nNode.getTextContent());
                tmp.mkdirs();
            }
        }
        FileUtils.createShortcut(newBusi, new File(newClient, "COMMERCIAL"));
        return NO_ERR;
    }

    public int addLife(File newClient) {
        File newLife = new File(createURL(cData, LIFE));
        if (newLife.exists())
            return POLICY_EXISTS;
        newLife.mkdirs();
        for (int i = 0; i < lifeFS.getLength(); i++) {
            Node nNode = lifeFS.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                File tmp = new File(newLife, nNode.getTextContent());
                tmp.mkdirs();
            }
        }
        FileUtils.createShortcut(newLife, new File(newClient, "LIFE"));
        return NO_ERR;
    }
}

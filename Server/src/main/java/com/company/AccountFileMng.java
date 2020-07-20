package com.company;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AccountFileMng {

    public static void LoadAccounts(ArrayList<Account> accounts,String FILE_ACC) {
        try {
            File file;
            file = new File(FILE_ACC);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            doc = dBuilder.parse(file);
            NodeList nodeList = doc.getElementsByTagName("record");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nNode;
                    String name, password;
                    name = element.getElementsByTagName("name").item(0).getTextContent();
                    password = element.getElementsByTagName("pass").item(0).getTextContent();
                    Account temp = new Account(name, password);
                    accounts.add(temp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void AddAccount(Account account, ArrayList<Account> accounts, String FILE_ACC) {
        accounts.add(account);
        try {
            File file;
            file = new File(FILE_ACC);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            doc = dBuilder.parse(file);

            Element root = doc.getDocumentElement();
            Element record = doc.createElement("record");
            root.appendChild(record);

            Element elementName = doc.createElement("name");
            Text nameText = doc.createTextNode(account.name);
            elementName.appendChild(nameText);
            record.appendChild(elementName);
            Element elementPass = doc.createElement("pass");
            Text passText = doc.createTextNode(account.pass);
            elementPass.appendChild(passText);
            record.appendChild(elementPass);

            Source source = new DOMSource(doc);
            Result result = new StreamResult(file);
            Transformer trans = TransformerFactory.newInstance().
                    newTransformer();
            trans.transform(source, result);
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
            accounts.remove(account);
        }
    }
}

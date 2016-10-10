package ESTest;

import ESTest.bean.IndexEntity;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by zhipengwu on 16-10-10.
 */
public class RunTest {
    public static void main(String[] args) {
        ESTest esTest =new ESTest();
        BufferedReader br=null;
        IndexEntity indexEntity = null;
        String index="searchanalysis121";
        String type="input";

        List<IndexEntity> indexEntityList= Lists.newArrayList();
        String file= RunTest.class.getClassLoader().getResource("log.json").getPath();
        try {
             br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        int id = 0;
        try {
            while ((line = br.readLine()) != null) {
                indexEntity = new IndexEntity();
                indexEntity.setIndex(index);
                indexEntity.setType(type);
                indexEntity.setId(String.valueOf(id++));
                indexEntity.setSource(line);
                indexEntityList.add(indexEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        esTest.createNotAnalyzedIndex(index,indexEntityList);

    }
}

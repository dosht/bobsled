package com.retail.read

import org.apache.spark.sql.{DataFrame, DataFrameReader}
import org.apache.spark.sql.types.StructType

package object excel {
  implicit class ExcelDataFrameReader(val dataFrameReader: DataFrameReader) extends AnyVal {
    def excel(file: String, sheetName: String, schema: StructType): DataFrame = dataFrameReader
      .format("excel") // Or .format("excel") for V2 implementation
      .option("dataAddress", sheetName) // Optional, default: "A1"
      .option("header", "true") // Required
      .option("treatEmptyValuesAsNulls", "false") // Optional, default: true
      .option("setErrorCellsToFallbackValues", "true") // Optional, default: false, where errors will be converted to null. If true, any ERROR cell values (e.g. #N/A) will be converted to the zero values of the column's data type.
      .option("usePlainNumberFormat", "false") // Optional, default: false, If true, format the cells without rounding and scientific notations
      .option("inferSchema", "false") // Optional, default: false
      .option("addColorColumns", "true") // Optional, default: false
      .option("timestampFormat", "MM-dd-yyyy HH:mm:ss") // Optional, default: yyyy-mm-dd hh:mm:ss[.fffffffff]
      .option("maxRowsInMemory", 20) // Optional, default None. If set, uses a streaming reader which can help with big files (will fail if used with xls format files)
      .option("maxByteArraySize", 2147483647) // Optional, default None. See https://poi.apache.org/apidocs/5.0/org/apache/poi/util/IOUtils.html#setByteArrayMaxOverride-int-
      .option("tempFileThreshold", 10000000) // Optional, default None. Number of bytes at which a zip entry is regarded as too large for holding in memory and the data is put in a temp file instead
      .option("excerptSize", 10) // Optional, default: 10. If set and if schema inferred, number of rows to infer schema from
      //  .option("workbookPassword", "pass") // Optional, default None. Requires unlimited strength JCE for older JVMs
      .schema(schema) // Optional, default: Either inferred schema, or all columns are Strings
      .load(file)
  }
}

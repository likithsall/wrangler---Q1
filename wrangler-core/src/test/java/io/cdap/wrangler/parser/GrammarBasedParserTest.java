/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */
package io.cdap.wrangler.parser;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.CompileStatus;
import io.cdap.wrangler.api.Compiler;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.RecipeParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests {@link GrammarBasedParser}
 */
public class GrammarBasedParserTest {
  
  @Test
  public void testBasic() throws Exception {
    String[] recipe = new String[] {
      "#pragma version 2.0;",
      "rename :col1 :col2",
      "parse-as-csv :body ',' true;",
      "#pragma load-directives text-reverse, text-exchange;",
      "${macro} ${macro_2}",
      "${macro_${test}}"
    };
    RecipeParser parser = TestingRig.parse(recipe);
    List<Directive> directives = parser.parse();
    Assert.assertEquals(2, directives.size());
  }
  
  @Test
  public void testLoadableDirectives() throws Exception {
    String[] recipe = new String[] {
      "#pragma version 2.0;",
      "#pragma load-directives text-reverse, text-exchange;",
      "rename col1 col2",
      "parse-as-csv body , true",
      "text-reverse :body;",
      "test prop: { a='b', b=1.0, c=true};",
      "#pragma load-directives test-change,text-exchange, test1,test2,test3,test4;"
    };
    Compiler compiler = new RecipeCompiler();
    CompileStatus status = compiler.compile(new MigrateToV2(recipe).migrate());
    Assert.assertEquals(7, status.getSymbols().getLoadableDirectives().size());
  }
  
  @Test
  public void testByteSizeParsing() throws Exception {
    String[] recipe = new String[] {
      "parse-as-csv :body;",
      "set-column :filesize 5MB;",
      "aggregate-stats :size :time total 10MB;"
    };
    
    // Parse the recipe
    RecipeParser parser = TestingRig.parse(recipe);
    List<Directive> directives = parser.parse();
    
    // Assert that we have the correct number of directives
    Assert.assertEquals(3, directives.size());
    
    // Check the second directive (set-column) for ByteSize argument
    Directive setColumnDirective = directives.get(1);
    Assert.assertEquals("set-column", setColumnDirective.name());
    Assert.assertEquals(2, setColumnDirective.args().length);
    Assert.assertEquals(":filesize", setColumnDirective.args()[0]);
    
    // The second argument should be a ByteSize token with value 5MB
    Object byteSizeArg = setColumnDirective.args()[1];
    Assert.assertTrue("Argument should be a ByteSize", byteSizeArg instanceof ByteSize);
    ByteSize byteSize = (ByteSize) byteSizeArg;
    Assert.assertEquals(5.0, byteSize.getNumericValue(), 0.001);
    Assert.assertEquals("MB", byteSize.getUnit());
    Assert.assertEquals(5_000_000, byteSize.getBytes());
    
    // Check the third directive (aggregate-stats) for ByteSize argument
    Directive aggregateDirective = directives.get(2);
    Assert.assertEquals("aggregate-stats", aggregateDirective.name());
    Assert.assertEquals(4, aggregateDirective.args().length);
    
    // The fourth argument should be a ByteSize token with value 10MB
    Object aggByteSizeArg = aggregateDirective.args()[3];
    Assert.assertTrue("Argument should be a ByteSize", aggByteSizeArg instanceof ByteSize);
    ByteSize aggByteSize = (ByteSize) aggByteSizeArg;
    Assert.assertEquals(10.0, aggByteSize.getNumericValue(), 0.001);
    Assert.assertEquals("MB", aggByteSize.getUnit());
    Assert.assertEquals(10_000_000, aggByteSize.getBytes());
  }
  
  @Test
  public void testTimeDurationParsing() throws Exception {
    String[] recipe = new String[] {
      "set-column :timeout 30S;",
      "set-column :execution_time 5M;",
      "sleep 100MS;"
    };
    
    // Parse the recipe
    RecipeParser parser = TestingRig.parse(recipe);
    List<Directive> directives = parser.parse();
    
    // Assert that we have the correct number of directives
    Assert.assertEquals(3, directives.size());
    
    // Check the first directive (set-column) for TimeDuration argument
    Directive timeoutDirective = directives.get(0);
    Assert.assertEquals("set-column", timeoutDirective.name());
    Object timeoutArg = timeoutDirective.args()[1];
    Assert.assertTrue("Argument should be a TimeDuration", timeoutArg instanceof TimeDuration);
    TimeDuration timeout = (TimeDuration) timeoutArg;
    Assert.assertEquals(30.0, timeout.getNumericValue(), 0.001);
    Assert.assertEquals("S", timeout.getUnit());
    Assert.assertEquals(30_000, timeout.getMilliseconds());
    
    // Check the second directive (set-column) for TimeDuration argument
    Directive executionDirective = directives.get(1);
    Assert.assertEquals("set-column", executionDirective.name());
    Object executionArg = executionDirective.args()[1];
    Assert.assertTrue("Argument should be a TimeDuration", executionArg instanceof TimeDuration);
    TimeDuration executionTime = (TimeDuration) executionArg;
    Assert.assertEquals(5.0, executionTime.getNumericValue(), 0.001);
    Assert.assertEquals("M", executionTime.getUnit());
    Assert.assertEquals(5 * 60 * 1000, executionTime.getMilliseconds());
    
    // Check the third directive (sleep) for TimeDuration argument
    Directive sleepDirective = directives.get(2);
    Assert.assertEquals("sleep", sleepDirective.name());
    Object sleepArg = sleepDirective.args()[0];
    Assert.assertTrue("Argument should be a TimeDuration", sleepArg instanceof TimeDuration);
    TimeDuration sleepTime = (TimeDuration) sleepArg;
    Assert.assertEquals(100.0, sleepTime.getNumericValue(), 0.001);
    Assert.assertEquals("MS", sleepTime.getUnit());
    Assert.assertEquals(100, sleepTime.getMilliseconds());
  }
}

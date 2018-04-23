/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bots;

/**
 *
 * @author Administrator
 */
public class StringMethods {
    String[]signs = {"+","-","*","/"};
    
   public static double formulate(String s){
        String num1="",num2="",sign = "none";
       boolean firstdigit = true,end = false, num1nsign = false;   
       s.replaceAll(" ", "");
       double answer = 639481039.1; // Randomly picked number to act as our dummy. 
      for(int i = 0; i < s.length() && !end; i++){
          if(!num1nsign){ // Finding the second number after getting the first number and the operand
          if(Character.isDigit(s.charAt(i))){ // Getting the first number
              if(firstdigit){
              num1 = s.charAt(i)+"";
              firstdigit = false;
                      }
              else{
                 num1+= s.charAt(i);
              }
          }
          else
          {
              if(!num1.isEmpty()){ // If it doesnt exist, I wont do anything
                  if(s.charAt(i) == '+' ||s.charAt(i) == '-' ||s.charAt(i) == '*' ||s.charAt(i) == '/'  ||s.charAt(i) == 'x'  ||s.charAt(i) == ':'){ // If right after the first number theres one of this sign, Ill save them and say that were searching for num2
                      sign = s.charAt(i)+"";
                      num1nsign = true;
                  }
                  else{ // If theres no operand, Ill just reset num1 and search for the first digit once again                      
                      num1 = "";
                  }
                   firstdigit = true;   
              }
          }
        } else{
           if(Character.isDigit(s.charAt(i))){ // Getting the first number
              if(firstdigit){
              num2 = s.charAt(i)+"";
              firstdigit = false;
                      }
              else{
                 num2+= s.charAt(i);
              }
          } else {
                  if(firstdigit){ // Checking to see if the first things after the operand is a number, if it isnt the formulas broken and thus the process begins once more!
                      num1nsign = false;
                      num1 = "";
                      sign = "none";
                  }
                  else{
                     end = true; // Found the formula  
                  }
              }            
        }
      }
      
      if(!num1.isEmpty() && !num2.isEmpty() && !sign.equals("none")) // Theres a formula
      {
         switch(sign){
             case "+":
                 answer = Double.parseDouble(num1) +  Double.parseDouble(num2);
                 break;
             case "-":
                 answer = Double.parseDouble(num1) -  Double.parseDouble(num2);
                 break;
             case "x":    
             case "*":
                 answer = Double.parseDouble(num1) *  Double.parseDouble(num2);
                 break;
             case "/":
                 answer = Double.parseDouble(num1) /  Double.parseDouble(num2);
                 break; 
         }
         
      }
      
      
      return answer;
    }
       
}

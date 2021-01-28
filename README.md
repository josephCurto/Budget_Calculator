# Budget Calculator w/GUI

A simple budget calculator used for budgeting, and tracking spending.

The program takes a given monthly income (**which you have to set in the Ops class**) and the ratio of your income you wish to allocate towards: 

    Examples:
        Expenses : Rent, Spotify, Groceries, Phone, Internet...
        Savings : PC savings, general savings...
        Investments : Apple, GameStop, Tesla...
        Particulars/Emergencies : Car, Medical...

* By default the income is set to 5K a month with:
    * Expenses : 50%
    * Savings : 40%
    * Investment : 5%
    * Particulars/Emergencies : 5%


View the code from the _MonthlyBudgetRatio()_ method below:

    float monthlyIncome = (float) 5000.00;
    monthlyExpenseGoal = (float) (monthlyIncome * 0.50);
    monthlySavingsGoal = (float) (monthlyIncome * 0.40);
    monthlyInvestmentGoal = (float) (monthlyIncome * 0.05);
    monthlyEmergencyGoal = (float) (monthlyIncome * 0.05);


## The process:
1. You create a txt file where you will create a entry with a transaction choice of:
    * E : Expense
    * S : Savings
    * I : Investment
    * P : Particulars/Emergency

2. In the transaction text box a user would insert a 'transaction' such as:
   
    E,AmazonShopping,10.00,1/4/2020 \
    ~~E,Amazon Shopping,10.00,1/4/2020~~ \
    ~~E , AmazonShopping , 10.00 , 1/4/2020~~

3. Then specify the path to the txt file where you have written the transactions down using the _'Choose File'_ button.

4. After you are satisfied with the amount of transactions you have input into your txt file, then you are ready to click the _'Calculate Budget'_ button.
   
5. Once the button has been pressed, a report will be generated (**STILL WORKING ON THE PATHING FOR THIS**) along with a bar chart and a line chart. \

    * The Bar Chart
        * Displays two groupings of bars, one dedicated to your monthly spending goal, and the other would be your actual spending that you documented.
    * The Line Chart
        * Displays your Expense spending trend over the



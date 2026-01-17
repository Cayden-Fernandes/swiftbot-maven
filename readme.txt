# Calibrate Your SwiftBot - Survey

Approach 2- measure Time taken to travel a fixed distance. 


## After I get the K=0.2367x through the graph
I then use, V **actual** = distance (cm)/ time (s)

1. **Cover 40cm in 3 Seconds**

V actual = 40/3

V actual ‎ = 13.333

*V Percentage = V actual/ K*

V Percentage = 13.33/0.2367

V Percentage ‎ = 56.316

1. **Cover 60cm in 8 Seconds**

V actual =60/8

V actual ‎ = 7.5

*V Percentage = V **actual**/ K*

V Percentage=7.5/0.2367

V percentage ‎ = 31.686

1. **Cover 80cm in 4 Seconds**

V actual =80/4

V actual‎ = 20

*V Percentage = V **actual**/ K*

V Percentage=20/0.2367

V percentage ‎ = 84.495

- Round to the nearest integer (Because  **`move()`** only takes `int` speeds)
##
I have kept the duration as 100 cm track (Check it in the Excel file I created), so you will need a measuring tape of aprox 50cm long or more. In this you will mark the start and the end point (I used a 100cm long measuring tape).

You will also require a code on which you will have to "Set" a movement speed for the swiftbot like `20%, 30%, 40% and so on....` So you also have to change the movement speed in the code itself accordingly while you do the other percentage speeds.

So next you start with `20%` speed, here you will have to measure from the start to the end point which in my case it is 50cm, how many seconds the SwiftBot took  to reach to the finish point to the end point.

Then based on the Graph you take the calculated `"K"` value and then you, use this formula: 

`V actual = distance(cm)/time(s)`

*so if you want to cover 40cm within 4 Seconds,* you will have to: `V actual = 40/4 which is: 10`

**so here**: `V actual = 10`

**Then you use this formula:** `V Percentage= V actual/ K`and then accordingly you will get the "V Percentage 

" ***BUT KEEP IN MIND YOU WILL NEED THE VALUE OF "K" WHICH YOU WILLL HAVE TO GET IT IN THE EALIER STEPS AS SPECIFIED ABOVE”***

Once you are done with that you will have to Determine the error margin associated with the calculated 𝑽𝒑𝒆𝒓𝒄𝒆𝒏𝒕𝒂𝒈𝒆 value.                              

So for that you use the previously used distances like the "Cover 40cm within 4 Seconds"

Here again you use the measuring setup and check whether based on the "**V Percentage**" calculated you actually cover exact 40cm or not, like if you cover more than 40cm based on the V Percentage Speed and the Time(s) for example you cover **`46cm or 36cm`**, you will have to write `+6cm` as Error margin OR `-4cm` Error Margin. The “+ OR - " will depend whether less distance is covered or more, like if less distance is cover you will use `"-"` if more you use `"+"`.
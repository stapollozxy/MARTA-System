import React from 'react';
import {SingleStep} from './SingleStep';
import {MultiStep} from './MultiStep';
import {Log} from './Log';
import {ResponseLogData} from './ResponseLogData';
import {ResponseLog} from './ResponseLog';
import {SysReport} from './SysReport';
import {AddVehicle} from './AddVehicle';
import {AddRoute} from './AddRoute';
import {AddStop} from './AddStop';
import {ExtendRoute} from './ExtendRoute';
import {AddEvent} from './AddEvent';
import {Quit} from './Quit';
import {UploadDatabase} from './UploadDatabase';

export class Main extends React.Component {
    state = {
      msg : 'start',
      rMsg: 'start',
      StringReportData: 'NoData',
    };

    transferMsg(msg, rMsg) {
        this.setState({
            msg, rMsg
        });
    }

    transferRmsg(rmsg) {
        this.setState({
            rmsg
        });
    }
    render() {
        return (
            <div>
                <section className='singlesim'>
                    <AddStop transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                    <AddRoute transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                    <ExtendRoute transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                    <AddVehicle transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                    <AddEvent transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                </section>

                <section className='multisim'>
                    <UploadDatabase transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                    <SingleStep transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                    <MultiStep transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                    <Quit transferMsg = {(msg, rMsg)=> this.transferMsg(msg, rMsg)}/>
                </section>

                <section className='report'>
                    <SysReport/>
                </section>

                <section className='log'>
                   <Log msg={this.state.msg}/>
                </section>

                <section className='responselog'>
                   <ResponseLog rmsg={this.state.rmsg}/>
                </section>

            </div>
        );
    }
}

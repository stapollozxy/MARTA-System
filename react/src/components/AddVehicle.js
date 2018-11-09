import React from 'react';
import $ from 'jquery';
import { Form, Icon, Input, Button, message } from 'antd';
import { API_ROOT } from '../constants';
import PropTypes from 'prop-types';
import {Log} from './Log';
import {LogData} from './LogData'
import {ResponseLogData} from './ResponseLogData'
const FormItem = Form.Item;


class AddBusForm extends React.Component {
    state = {
        confirmDirty: false,
        autoCompleteResult: [],
        passenger: 0,
    };
    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);

                var d = LogData.getData();
                var rld = ResponseLogData.getData();
                var temp = {number: d.length, content:'add transit,'+ values.VehicleID + ',' + values.Routes + ',' + values.Location + ',' + values.Passenger + ',' + values.Capacity};
                d.push(temp);
                LogData.setData(d);
                var parent = this;
                this.props.transferMsg(d);
                $.ajax({
                    url: `${API_ROOT}/command`,
                    method: 'POST',
                    data: 'add_vehicle,'+ values.VehicleID + ',' + values.Routes + ',' + values.Location + ',' + values.Passenger + ',' + values.Capacity,

                }).then((response) => {
                    message.success("transit added");
                    var tmp = {number: rld.length, content:response};
                    rld.push(tmp);
                    //this.props.transferMsg(d, rld).bind(window);
                    parent.props.transferMsg(d, rld);
                }, (response) => {
                    message.error(response.responseText);
                }).catch((error) => {
                    message.error(error);
                });
            }
        });
    }

    checkNum = (rule, value, callback) => {
        const form = this.props.form;
        if (value && !(value < Number.MAX_SAFE_INTEGER && value >= 0 )) {
            callback('Invalid Number');
        } else {
            callback();
        }
    }

    checkCapacity = (rule, value, value2, callback) => {
        const form = this.props.form;
        if (value && !(value < Number.MAX_SAFE_INTEGER && value >= 0 ) || value < value2) {
            callback('Invalid Capacity');
        } else {
            callback();
        }
    }

    checkConfirm = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(['confirm'], { force: true });
        }
        callback();
    }
    render() {
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {
                xs: { span: 24 },
                sm: { span: 8 },
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 16 },
            },
        };
        const tailFormItemLayout = {
            wrapperCol: {
                xs: {
                    span: 24,
                    offset: 0,
                },
                sm: {
                    span: 16,
                    offset: 8,
                },
            },
        };
        return (
            <Form onSubmit={this.handleSubmit} className="multisim-form">
                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit">Add Vehicle</Button>
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Vehicle ID"
                >
                    {getFieldDecorator('VehicleID', {
                        rules: [{ required: true, message: 'Please input VehicleID!', whitespace: true }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Route ID"
                >
                    {getFieldDecorator('Routes', {
                        rules: [{ required: true, message: 'Please input your route!', whitespace: true }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Location"
                >
                    {getFieldDecorator('Location', {
                        rules: [{ required: true, message: 'Please input location!', whitespace: true }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Passenger"
                >
                    {getFieldDecorator('Passenger', {
                        rules: [{ required: true, message: 'Please input passengers!', whitespace: true }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Capacity"
                >
                    {getFieldDecorator('Capacity', {
                        rules: [{
                            required: true, message: 'Please input capacity!'}, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

            </Form>
        );
    }
}


export const AddVehicle = Form.create()(AddBusForm);